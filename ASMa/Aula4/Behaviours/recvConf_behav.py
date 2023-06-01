from spade.behaviour import CyclicBehaviour

class RecvConf_Behav (CyclicBehaviour):
    
    async def run(self):
        msg = await self.receive(timeout=10)  # wait for a message for 10 seconds

        if msg:
            # Message Treatment based on different ACLMessage performatives
            performative = msg.get_metadata('performative')
            print("--------------------------------\n")
            if performative == 'inform':
                print("Agent {}:".format(str(self.agent.jid)) + " Reply INFORM = Product {}".format(msg.body))

            elif performative == 'confirm':
                print("Agent {}:".format(str(self.agent.jid)) + " Reply CONFIRM = Product {}".format(msg.body))

            elif performative == 'refuse':
                print("Agent {}:".format(str(self.agent.jid)) + " Reply REFUSE = Product {}".format(msg.body))
        else:
            print("Agent {}:".format(str(self.agent.jid)) + " Did not received any message after 10 seconds")
            self.kill()
    
    #async def on_end(self):
    #    await self.agent.stop()