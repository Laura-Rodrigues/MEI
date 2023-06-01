import numpy as np
from dataset import Dataset
from layer import Layer


class DNN:

    def __init__(self, dataset, normalize = False):
        self.X, self.y = dataset.getXy()
        self.X = np.hstack ( (np.ones([self.X.shape[0],1]), self.X ) )
        
        self.layers = []
        
        if normalize:
            self.normalize()
        else:
            self.normalized = False

    def setLayer(self,layer):
        self.layers.append(layer)
    
    
    def predict(self, instance):
        x = np.empty([self.X.shape[1]])        
        x[0] = 1
        x[1:] = np.array(instance[:self.X.shape[1]-1])
        
        if self.normalized:
            if np.all(self.sigma!= 0): 
                x[1:] = (x[1:] - self.mu) / self.sigma
            else: x[1:] = (x[1:] - self.mu)
        
        a2 = x
        for layer in self.layers:
            if(layer != self.layers[-1]):
                a2 = layer.predict_layer(a2,False)
            else:
                a2 = layer.predict_layer(a2,True)
        
        return a2


    def costFunction(self, weights = None):
        if weights is not None:
            lower = 0
            upper = 0
            i=0
            for layer in self.layers:
                if(layer != self.layers[-1]):
                    upper += (layer.h+1) * self.layers[i+1].h
                    layer.W = weights[lower:upper].reshape([self.layers[i+1].h, layer.h+1])
                    lower = upper
                else:
                    self.layers[i].W = weights[lower:].reshape([1, self.layers[-1].h+1])
                i += 1

        predictions = self.X

        for layer in self.layers:
            if(layer != self.layers[-1]):
                predictions = layer.costFunction_layer(predictions,False)
            else:
                predictions = layer.costFunction_layer(predictions,True)

        m = self.X.shape[0]
        sqe = (predictions - self.y.reshape(m,1)) ** 2
        res = np.sum(sqe) / (2*m)
        return res
        

    def build_model(self):
        from scipy import optimize
        
        size = 0
        i = 0
        for layer in self.layers:
            if(layer != self.layers[-1]):
                size += (layer.h+1) * self.layers[i+1].h
                print(f"size na layer: {size}")
            else:
                size += self.layers[i].h + 1
            i += 1
        print(f"size final: {size}\n\n")

        initial_w = np.random.rand(size)  
        result = optimize.minimize(lambda w: self.costFunction(w), initial_w, method='BFGS', 
                                    options={"maxiter":1000, "disp":False} )
        
        weights = result.x
        lower = 0
        upper = 0
        i = 0
        for layer in self.layers:
            if(layer != self.layers[-1]):
                upper += (layer.h+1) * self.layers[i+1].h
                layer.W = weights[lower:upper].reshape([self.layers[i+1].h, layer.h+1])
                lower = upper
            else:
                layer.W = weights[lower:].reshape([1, layer.h+1])
            i += 1


    def normalize(self):
        self.mu = np.mean(self.X[:,1:], axis = 0)
        self.X[:,1:] = self.X[:,1:] - self.mu
        self.sigma = np.std(self.X[:,1:], axis = 0)
        self.X[:,1:] = self.X[:,1:] / self.sigma
        self.normalized = True

def sigmoid(x):
  return 1 / (1 + np.exp(-x))

def test(): 
    ds= Dataset("xnor.data")
    nn = DNN(ds)
    nn.setLayer(Layer(hidden_nodes=2, weights=np.array([[-30,20,20],[10,-20,-20]])))
    nn.setLayer(Layer(hidden_nodes=1, weights=np.array([[-10,20,20]])))
    print( nn.predict(np.array([0,0]) ) )
    print( nn.predict(np.array([0,1]) ) )
    print( nn.predict(np.array([1,0]) ) )
    print( nn.predict(np.array([1,1]) ) )
    print(nn.costFunction(None))

def test2():
    ds= Dataset("xnor.data")
    nn = DNN(ds, normalize = False)
    print("Camada 1 com 2 nodos ")
    nn.setLayer(Layer(hidden_nodes=2))
    print("Camada 2 com 3 nodos ")
    nn.setLayer(Layer(hidden_nodes=3))
    print("Camada 3 com 1 nodos ")
    nn.setLayer(Layer(hidden_nodes=1))
    #print("Camada 4 com 6 nodos ")
    #nn.setLayer(Layer(hidden_nodes=6))
    nn.build_model()
    print( nn.predict(np.array([0,0]) ) )
    print( nn.predict(np.array([0,1]) ) )
    print( nn.predict(np.array([1,0]) ) )
    print( nn.predict(np.array([1,1]) ) )
    print(nn.costFunction(None))
    
print("Test 1: Pesos definidos: ")
test()
print("\n\n------------------\n\n\nTest 2: Build model: ")
test2()