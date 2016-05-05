#############################################################################
#some preliminary code to get parameter estimates, etc....  This will mostly be irrelevant, except for the reading in of the parameters which will need to be done.
#############################################################################
#So, things from this section that are relevant for web implimentation.  1. I used a multivariate normal distribution to model the mesh size and twine size, as they covary.
#We could simplify this and just use two normals, but it would not be as close to the fitted model.  So, we need to get a multivariate normal function in python, java or something.
#2.  I created some simulated data for model testing.  That might be useful.  3.  The final data should have 4 vectors, mesh size, twine size, color, and construction.  Note
#that the characteristics have a specific format and need to match those in the data that I used for fitting (data2 in the R workspace).  The characteristics in the data were
#Color: GREY   BLUE   WHITE  GREEN  BROWN  RED    BLACK  CLEAR  ORANGE YELLOW.  You can find the exact ones if you look in the header of the "Cluster categorical parameters" file.
#Construction: amalgamated from Riki's characteristics.  Twisted 3 Single  Twisted 5 Single  Twisted 4 Single  Braided Single    Twisted 3 Double  Mono Single       Twisted 13 Single Twisted 2 Single 
#Twisted 2 Double  Twisted 5 Double  Twisted 4 Double.  You can find these if you look at the header on the "Cluster categorical parameters" file.
#Mesh size: in mm, continuous
#Twine size:  in mm, continuous


#clear workspace
rm(list = ls())

#get multivariate normal library
library(mvtnorm)

#set the working directory
setwd("/Users/wil02p/Desktop/Parallels shared folder/Recovered net cluster analysis/Parameters")

#load the old workspace with the dll etc
load("/Users/wil02p/Desktop/Parallels shared folder/Recovered net cluster analysis/current workspace 2013.07.29")

#the model for predicting catch rates by clusters is called M in the R workspace.  
write.table(M$coefficients, "/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting/Model coefficients for catch prediction", sep = ",", col.names = T)

#the cluster analysis that was best used 14 clusters.  Need to write out the cluster parameters to a file, so we can read them back and use them
write.table(ClusterFits[[14]]$model$disc, "/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting/Cluster categorical parameters", sep = ",", col.names = T)
write.table(ClusterFits[[14]]$model$Mu, "/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting/Cluster continuous parameters means", sep = ",", col.names = T)
write.table(ClusterFits[[14]]$model$Sigma, "/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting/Cluster continuous parameters sigmas", sep = ",", col.names = T)



#############################################################################
#Code to make predictions of which cluster a net is in
#############################################################################
#set the working directory.  This has to be done outside the function
setwd("/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting")

GetClusterPredictions <- function(Data){
#Function to make predictions of which cluster a net is in.  Takes in an object called Data, which 
#has observations in rows, and net characteristics in columns.  It returns the cluster number a 
#net is in, based on its characteristics.  It returns the most likely cluster, and the probabilities of
#each cluster, appended to the original Data.  Note that names in Data need to be correct.  They
#are: MeshSize, TwineSize, Colors, Construction.  See above in script for simulated data as example. 
#
#Create simulated data for testing.  Assume a set of vectors, with corresponding rows equating to a single net.  
#So element 1 in each is the data corresponding to net 1
#set the working directory
#setwd("/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting/Parameters")
#source the parameters for the cluster allocation
#ContinuousMeans <- matrix(unlist(read.table("Cluster continuous parameters means", sep = ",", header = T)), ncol = 2)
#ContinuousSigmas <- array(unlist(read.table("Cluster continuous parameters sigmas", sep = ",", header = T)), dim = c(2,2,14))
#DiscreteProbs <- read.table("Cluster categorical parameters", sep = ",", header = T)
#MeshSize <- rnorm(100, ContinuousMeans[1,1], sd =1)
#TwineSize <- rnorm(100, ContinuousMeans[1,2], sd = 1)
#Colors <- sample(colnames(DiscreteProbs)[1:10],100,replace = T)
#Construction <- sample(colnames(DiscreteProbs)[11:21],100,replace = T)
#Data <- data.frame(cbind(MeshSize,TwineSize,Colors,Construction), stringsAsFactors = F)

#get multivariate normal library
library(mvtnorm)

#source the parameters for the cluster allocation
ContinuousMeans <- matrix(unlist(read.table("Parameters/Cluster continuous parameters means", sep = ",", header = T)), ncol = 2)
ContinuousSigmas <- array(unlist(read.table("Parameters/Cluster continuous parameters sigmas", sep = ",", header = T)), dim = c(2,2,14))
DiscreteProbs <- read.table("Parameters/Cluster categorical parameters", sep = ",", header = T)


#Note that the dimensions of these depend on the fitted model, so if the model for clustering is refit, then you need to revisit these colors
#Now, step through the data and assign a probability for each of the 14 types for each record.  Need to create a variable to hold the probs
#then join it to Data at the end.  
TypeProbs <- matrix(nrow = dim(Data)[1], ncol = 14)
for(i in 1:dim(Data)[1]){
#cycle through the mesh and twine size parameters for the 14 possible types, get the density for each and then normalize to a probability
SizeProbs <- vector(length = 14)
for(j in 1:14){
SizeProbs[j] <- dmvnorm(c(MeshSize[i],TwineSize[i]), mean = ContinuousMeans[j,], sigma = ContinuousSigmas[,,j])
}
#check if the size probs are greater that zero, if not, then the probs should stay NA
if (sum(SizeProbs >0)) SizeProbs <- SizeProbs/sum(SizeProbs)

#now take the product of the color probs, construction probs, and mesh-twine size probs for each type.  Note that I have normalized the values
#from the discrete probs at the moment as they did not sum to 1. Should probably go back and check those.
index <- match(c(Colors[i],Construction[i]),colnames(DiscreteProbs))
TypeProbs[i,1:14] <- DiscreteProbs[,index[1]]/sum(DiscreteProbs[,index[1]]) * DiscreteProbs[,index[2]]/sum(DiscreteProbs[,index[2]]) * SizeProbs

}

#get the sum of the probabilities across the types, this is to some extent a measure of the quality of the assignment
TotalProbs <- matrix(rowSums(TypeProbs), ncol = 1)

#bind together the data, the type probabilities, and the total probability across types
DataWAssignments <- cbind(Data,TotalProbs,TypeProbs)

return(DataWAssignments)

}

#############################################################################
#Code to make predictions of catch rates
#############################################################################
#set the working directory.  This has to be done outside the function
setwd("/Users/wil02p/Documents/Data/Projects/Marine Debris/Ghost Nets/Online database and reporting")

GetCatchPrediction <- function(Data){
#This function predicts turtle catch based on our work, given net characteristics.  Takes in an object 
#called Data, which has observations in rows, and net characteristics in columns.  Data needs to contain:
#length, twine size, mesh size, mono/multi, twisted/braided, number strands, single/double twine.  The
#syntax matters, so the names formally are: length, twine.size, mesh.size, mono.multi, twisted.braided, 
#single.double, strands.
#
#This simulates data for testing, currently commented out. 
#length <- rnorm(100, 5,1)
#twine.size <- rnorm(100,4,1)
#mesh.size <- rnorm(100,200,10)
#mono.multi <- sample(c("mono","multi"),100, replace = T)
#twisted.braided <- sample(c("twisted","braided"),100, replace = T)
#single.double <- sample(c("single","double"),100, replace = T)
#strands <- round(runif(100,1,13))  #note there are combinations that did not occurr in the data, so these will return NA I believe in this code
#Data <- data.frame(length, twine.size, mesh.size, mono.multi, twisted.braided, single.double, strands)
#end simulated data


#create variables to hold output
p <- rep(NA, times = length(Data$mesh.size))
expected.catch  <-rep(NA, times = length(Data$mesh.size))

for (i in 1:length(length)){
#initialize p.1 and p.2, necessary so that you get NA cases where a net type cant exist
p.1 <- rep(NA,1)
p.2 <- rep(NA,1)

#make prediction, first source the coefficients
coefficient.estimates <- read.table("Parameters/Model coefficients for catch prediction", sep = ",")

#first, get coefficients that are always in the equation
p.1 <- coefficient.estimates[[1]][1] + coefficient.estimates[[1]][2] * Data$length[i] + coefficient.estimates[[1]][3] * Data$length[i]^2 + coefficient.estimates[[1]][4]*Data$twine.size[i] + coefficient.estimates[[1]][5]*Data$mesh.size[i]

#now work out the net design and get the right coefficients to add to p based on that
if(Data$mono.multi[i] == "mono"){
p.2 <- coefficient.estimates[[1]][6]
} 

if(Data$mono.multi[i] =="multi"){
	#these are multistrand nets, which are braided or twisted
	if(Data$twisted.braided[i] == "twisted"){
		if(Data$single.double[i] == "single"){
			#single stranded nets, use a switch-case statement to get the right coefficient
			p.2 <- switch(Data$strands[i],NA,coefficient.estimates[[1]][9],coefficient.estimates[[1]][11],coefficient.estimates[[1]][12],coefficient.estimates[[1]][13],NA,NA,NA,NA,NA,NA,NA,coefficient.estimates[[1]][7])
		} else {
			#double stranded nets, use a switch-case statement to get the right coefficient
			p.2 <- switch(Data$strands[i],NA,coefficient.estimates[[1]][8],coefficient.estimates[[1]][10],NA,NA,NA,NA,NA,NA,NA,NA,NA,NA)
	}
}

#calculate the final probability that the net catches something, note that this is the probability/meter of net.  I believe it is also on the logit scale
#so needs to be rescaled
if(!is.na(p.1) & !is.na(p.2)) p[i] <- exp(p.1 + p.2)/(1 + exp(p.1 + p.2))

#calculate the expected number of animals.  Note that this assumes that each meter of net is basically an independent draw
#so it is a bit simplistic mathematically
expected.catch[i] <- p[i] * length[i]
}
}

#append expected catch to data and return
DataWExpectedCatch <- cbind(Data,expected.catch)

return(DataWExpectedCatch)
}

#############################################################################
#Code to make predictions of capture rates, by cluster
#############################################################################
#This could be added, but the parameters are probably better to use directly.  Currently not done.






