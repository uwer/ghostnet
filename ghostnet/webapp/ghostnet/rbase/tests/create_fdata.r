MeshSize <- rnorm(100, ContinuousMeans[1,1], sd =1)
TwineSize <- rnorm(100, ContinuousMeans[1,2], sd = 1)
Construction <- sample(colnames(DiscreteProbs)[11:21],100,replace = T)
Colors <- sample(colnames(DiscreteProbs)[1:10],100,replace = T)
Data1 <- data.frame(cbind(MeshSize,TwineSize,Colors,Construction), stringsAsFactors = F)


#This simulates data for testing, currently commented out. 
length <- rnorm(100, 5,1)
twine.size <- rnorm(100,4,1)
mesh.size <- rnorm(100,200,10)
mono.multi <- sample(c("mono","multi"),100, replace = T)
twisted.braided <- sample(c("twisted","braided"),100, replace = T)
single.double <- sample(c("single","double"),100, replace = T)
#note there are combinations that did not occurr in the data, so these will return NA I believe in this code
strands <- round(runif(100,1,13))  
Data2 <- data.frame(length, twine.size, mesh.size, mono.multi, twisted.braided, single.double, strands)
