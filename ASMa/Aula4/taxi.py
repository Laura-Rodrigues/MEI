import random

from spade.agent import Agent
from spade.behaviour import PeriodicBehaviour
from spade.message import Message

class Taxi(Agent):

    x_loc = random.uniform(0, 100)
    y_loc = random.uniform(0, 100)
    available = True