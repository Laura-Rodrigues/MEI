import numpy as np

class Layer:
    
    def __init__(self, hidden_nodes, weights = None):
    
        self.h = hidden_nodes
        self.W = weights
        #self.W1 = np.zeros([hidden_nodes, self.X.shape[1]])
        #self.W2 = np.zeros([1, hidden_nodes+1])

    def predict_layer(self, input, last):
        if(last == False):
            z2 = np.dot(self.W, input)
            a2 = np.empty([z2.shape[0]+1])
            a2[0] = 1
            a2[1:] = sigmoid(z2)
        else:
            a2 = np.dot(self.W,input)
            a2 = sigmoid(a2)
        return a2
    
    def costFunction_layer(self, input, last): 
        
        Z2 = np.dot(input, self.W.T)
        if(last == False):
            A2 = np.hstack((np.ones([Z2.shape[0],1]),sigmoid(Z2)))
        else:
            A2 = sigmoid(Z2)

        return A2


def sigmoid(x):
  return 1 / (1 + np.exp(-x))