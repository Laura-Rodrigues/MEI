import time
from spade import quit_spade

from Agents.helloAgent import helloAgent
from Agents.receiverAgent import ReceiverAgent
from Agents.senderAgent import SenderAgent

XMPP_SERVER = "lau-asus"
PASSWORD = "lau"

if __name__ == '__main__':

    # create agent instance and connect it to XMPP server
    helloworld_agent = helloAgent("lau@"+XMPP_SERVER, PASSWORD)

    # start agent instance process, returning a future (or promise) which you can wait for with the result method (future.result())
    future = helloworld_agent.start()
    future.result()
    
    # stop agent instance process
    helloworld_agent.stop()

    quit_spade()