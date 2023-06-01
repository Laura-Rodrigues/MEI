import datetime
import random

from spade.agent import Agent
from spade.behaviour import PeriodicBehaviour
from spade.message import Message

class BuyerAgent(Agent):
    products = ['Apple', 'Banana', 'Grapefruit', 'Orange', 'Pear', 'Melon', 'Strawberry']

    class InformBehav(PeriodicBehaviour):

        async def on_end(self):
            await self.agent.stop()

        async def on_start(self):
            self.counter = 0

        async def run(self):
            amount = random.randint(1,5)
            product = random.choice(self.agent.products)

            print(f'{self.counter} -> Sending request of product {product}, amount = {amount} at {datetime.datetime.now().time()}')
            msg = Message(to = self.get("seller_jid"))
            msg.body = str(product) + ":" + str(amount)

            await self.send(msg)

            if self.counter == 5:
                self.kill()
            self.counter += 1


    async def setup(self):
        print("Agent {}".format(str(self.jid)) + " starting...")

        start_at = datetime.datetime.now() + datetime.timedelta(seconds=5)
        b = self.InformBehav(period=2, start_at=start_at)
        self.add_behaviour(b)