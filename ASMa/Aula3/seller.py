import random

from spade.agent import Agent
from spade.behaviour import CyclicBehaviour

from Behaviours.profReview_Behav import ProfReviewBehav
from Behaviours.receiveRequests_Behav import ReceiveRequestBehav

class SellerAgent(Agent):

    products = ['Apple','Banana', 'Grapefruit', 'Orange']
    products_sold = {}
    products_value = {}

    async def setup(self):
        print("Agent {}".format(str(self.jid)) + " starting...")

        # initialise quantity of products sold per product in the list provided
        for i in self.products:
            self.products_sold[i] = 0                           # all products yet not sold, i.e., equal to 0 for each product
            self.products_value[i] = random.randint(1, 10)      # define a random cost for each product

        a = ReceiveRequestBehav()                   # CyclicBehav to verify buy requests from clients
        b = ProfReviewBehav(period=10)              # Every 10 sec, PeriodicBehav will calculate profit


        self.add_behaviour(a)
        self.add_behaviour(b)
