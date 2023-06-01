import getpass
import time

from spade import quit_spade
from buyer import BuyerAgent
from seller import SellerAgent


if __name__ == "__main__":
    
    buyer_jid = "buyer@lau-asus"
    passwd = "lau"
    buyer = BuyerAgent(buyer_jid, passwd)

    seller_jid = "seller@lau-asus"
    passwd = "lau"
    seller = SellerAgent(seller_jid, passwd)

    buyer.set('seller_jid', seller_jid)

    future = seller.start()
    future.result()  # wait for receiver agent to be prepared.

    fut = buyer.start()
    fut.result()
    
    while seller.is_alive():
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            buyer.stop()
            seller.stop()
            break
    print("Agents finished")
    quit_spade()