#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: miguelrocha
"""

from IPython.display import display
from math import floor
import numpy as np
import matplotlib.pyplot as plt

class Dataset:
    
    # constructor
    def __init__(self, filename = None, X = None, Y = None):
        if filename is not None:
            self.readDataset(filename)
        elif X is not None and Y is not None:
            self.X = X
            self.Y = Y
        else:
            self.X = None
            self.Y = None
        
        self.Xst = None
        
    def readDataset(self, filename, sep = ","):
        data = np.genfromtxt(filename, delimiter=sep)
        self.X = data[:,0:-1]
        self.Y = data[:,-1]
        
    def getXy (self):
        return self.X, self.Y
    
    def nrows(self):
        return self.X.shape[0]
    
    def ncols(self):
        return self.X.shape[1]
    
    def standardize(self):
        self.mu = np.mean(self.X, axis = 0)
        self.Xst = self.X - self.mu
        self.sigma = np.std(self.X, axis = 0)
        self.Xst = self.Xst / self.sigma
    
    def plotData2vars(self, xlab, ylab, standardized = False):
        if standardized:
            plt.plot(self.Xst, self.Y, 'rx', markersize=7)
        else:
            plt.plot(self.X, self.Y, 'rx', markersize=7)
        plt.ylabel(ylab)
        plt.xlabel(xlab)
        plt.show()
    
    def plotBinaryData(self):
        negatives = self.X[self.Y == 0]
        positives = self.X[self.Y == 1]
        plt.xlabel("x1")
        plt.ylabel("x2")
        plt.xlim([self.X[:,0].min(), self.X[:,0].max()])
        plt.ylim([self.X[:,0].min(), self.X[:,0].max()])
        plt.scatter( negatives[:,0], negatives[:,1], c='r', marker='o', linewidths=1, s=40, label='y=0' )
        plt.scatter( positives[:,0], positives[:,1], c='k', marker='+', linewidths=2, s=40, label='y=1' )
        plt.legend()
        plt.show()

    def split(self, test_size, random_state):
        np.random.seed(random_state)

        ind_test = np.random.choice(range(self.X.shape[0]), size=[int(self.X.shape[0]*test_size)], replace=False)
        ind_train = []
        for x in range(self.X.shape[0]): 
            if x not in ind_test:
                ind_train.append(x)

        test = Dataset(X=self.X[ind_test,:],Y=self.Y[ind_test])
        train = Dataset(X=self.X[ind_train,:],Y=self.Y[ind_train])
        return test, train


if __name__ == "__main__":

    def test():
        d = Dataset("lr-example1.data")
        #d.plotData2vars("Population", "Profit")
        #print(d.getXy())
        test, train = d.split(0.3,2023)
        print(test.getXy())
        print("\n\n\n")
        print(train.getXy())

    def testStandardized():
        d = Dataset("lr-example1.data")
        d.standardize()
        d.plotData2vars("Population", "Profit", True)
        print(d.getXy())
    
    def testBinary():
        ds= Dataset("log-ex1.data")   
        ds.plotBinaryData()   
        
    def testBinary2():
        ds= Dataset("log-ex2.data")   
        ds.plotBinaryData()   
    
    test()
    #testStandardized()   
    #testBinary()
    #testBinary2()
