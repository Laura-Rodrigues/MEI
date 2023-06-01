from spade import agent, quit_spade

class helloAgent(agent.Agent):
    async def setup(self):
        print("Agent {}".format(str(self.jid)) + " starting...")


#dummy = helloAgent("lau@lau-asus", "your_password")
#future = dummy.start()
#future.result()
#
#dummy.stop()
#quit_spade()