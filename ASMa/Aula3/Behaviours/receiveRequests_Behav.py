from spade.behaviour import CyclicBehaviour

class ReceiveRequestBehav(CyclicBehaviour):
    
    async def run(self):
        msg = await self.receive(timeout=100)  # wait for a message for 100 seconds
        if msg:
            print("Message received with content: {}".format(msg.body))
            split_msg = msg.body.split(":")
            product = split_msg[0]
            amount = int(split_msg[1])
            
            if product in self.agent.products:
                self.agent.products_sold[product] += amount
        else:
            print("\nDid not received any message after 100 seconds")
            self.kill()
    
    async def on_end(self):
        await self.agent.stop()