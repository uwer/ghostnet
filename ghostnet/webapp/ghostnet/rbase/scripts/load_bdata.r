

ContinuousMeans <- matrix(unlist(read.table("continuous_means.txt", sep = ",", header = T)), ncol = 2)
ContinuousSigmas <- array(unlist(read.table("continuous_sigmas.txt", sep = ",", header = T)), dim = c(2,2,14))
DiscreteProbs <- read.table("categorical.txt", sep = ",", header = T)

coefficient.estimates <- read.table("coefficients.txt", sep = ",")
