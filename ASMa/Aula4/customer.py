import random

from spade.agent import Agent
from spade.behaviour import PeriodicBehaviour
from spade.message import Message

class Customer(Agent):

    x_pos = random.uniform(0, 100)
    y_pos = random.uniform(0, 100)
    x_dest = random.uniform(0, 100)
    y_dest = random.uniform(0, 100)

    #def __str__ (self):
    #    return "cliente"