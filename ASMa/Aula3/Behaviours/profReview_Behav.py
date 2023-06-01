from spade.behaviour import PeriodicBehaviour

class ProfReviewBehav (PeriodicBehaviour):
    async def run(self):
        # initiate profit value
        profit = 0

        # calculate profit based on products value and products sold 
        for i in self.agent.products:
            print(f"{i}: {self.agent.products_sold[i]}")
            profit += (self.agent.products_sold[i] * self.agent.products_value[i])

        print("-----------------------------------------\n")
        print("-----------------------------------------\n")
        print("Agent {}:".format(str(self.agent.jid)) + "Profit = {}".format(profit))
        print("-----------------------------------------\n")
        print("-----------------------------------------\n\n")
        