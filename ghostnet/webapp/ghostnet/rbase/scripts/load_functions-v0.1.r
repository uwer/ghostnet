GetClusterPredictions <- function(Data){
#Function to make predictions of which cluster a net is in.  Takes in an object called Data, which 
#has observations in rows, and net characteristics in columns.  It returns the cluster number a 
#net is in, based on its characteristics.  It returns the most likely cluster, and the probabilities of
#each cluster, appended to the original Data.  Note that names in Data need to be correct.  They
#are: MeshSize, TwineSize, Colors, Construction.  See above in script for simulated data as example. 

#get multivariate normal library
library(mvtnorm)


#source of the parameters for the cluster allocation - THESE ARE EXPECTED TO BE SET GLOBALLY
## UR we expect to be in the correct directory containing these files
#ContinuousMeans <- matrix(unlist(read.table("continuous_means.txt", sep = ",", header = T)), ncol = 2)
#ContinuousSigmas <- array(unlist(read.table("continuous_sigmas.txt", sep = ",", header = T)), dim = c(2,2,14))
#DiscreteProbs <- read.table("categorical.txt", sep = ",", header = T)


#Note that the dimensions of these depend on the fitted model, so if the model for clustering is refit, then you need to revisit these colors
#Now, step through the data and assign a probability for each of the 14 types for each record.  Need to create a variable to hold the probs
#then join it to Data at the end.  
TypeProbs <- matrix(nrow = dim(Data)[1], ncol = 14)
for(i in 1:dim(Data)[1]){
 #cycle through the mesh and twine size parameters for the 14 possible types, get the density for each and then normalize to a probability
 SizeProbs <- vector(length = 14)
 for(j in 1:14){
  SizeProbs[j] <- dmvnorm(c(Data[i,1] and Data[i,2]), mean = ContinuousMeans[j,], sigma = ContinuousSigmas[,,j])
 }
 #check if the size probs are greater then zero, if not, then the probs should stay NA
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
## UR expected to be a global VAR
#coefficient.estimates <- read.table("Parameters/Model coefficients for catch prediction", sep = ",")

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