plot(c(0,1), c(0,1), type="n", xlab=expression(paste("mixing parameter (", omega, ")")), ylab=expression(paste("ratio of clusters sizes ( ", y, ")")))
names<-c("kmedoids", "hclust", "cluto", "sec")
namesL<-c("k-medoids", "hclust", "k-way (Cluto)", "SEC")
colors<-c("gray", "blue", "green", "red")
dir<-"D:\\Dropbox\\MMC-praca\\dane\\model-simplification\\res\\"
pre<-"data-"
for(j in 1:length(names)){
  tab1 <- t(as.matrix(read.table(paste0(dir, pre, names[j], ".txt"))))
  for(i in 1:101){
    tab1[1,i] = tab1[1,i]*0.01
    tab1[2,i] = tab1[2,i]
    if(i > 50){
      tab1[2,i] = 1-tab1[2,i]
    }
  }
  lines(tab1[1,], tab1[2,], col = colors[j], type="l")
}
legend(0.7,1, legend = namesL, col = colors, lwd = 1, text.col = colors, bty="n")


plot(c(0,1), c(0,1), type="n", xlab=expression(paste("dimensions ratio (", frac(d,D), ")")), ylab=expression(paste("ratio of clusters sizes ( ", y, ")")))
pre<-"dim-"
for(j in 1:length(names)){
  tab1 <- t(as.matrix(read.table(paste0(dir, pre, names[j], ".txt"))))
  for(i in 1:101){
    tab1[1,i] = (i-1)*0.01
    tab1[2,i] = tab1[2,i]
    if(i > 50){
      tab1[2,i] = 1-tab1[2,i]
    }
  }
  lines(tab1[1,], tab1[2,], col = colors[j], type="l")
  
}
legend(0.7,1, legend = namesL, col = colors, lwd = 1, text.col = colors, bty="n")



