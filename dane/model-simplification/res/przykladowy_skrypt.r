labels <- c("X1","X2","X3")
data3 <- read.table("przykladowe_dane.txt", header=T, sep="\t")
x <- barplot(as.matrix(data3), main="", ylab= "Miara", xaxt="n", xlab="",font.lab=2,
   beside=TRUE, col=c("red","blue","green","grey","pink"))
text(x[3,], par("usr")[3], labels = labels, srt = 45, adj = c(1.1,1.1), xpd = TRUE, cex=.9,font=2)
legend("topleft", c("1","2","3","4","5"), cex=0.6, 
   bty="n", fill=c("red","blue","green","grey","pink"));