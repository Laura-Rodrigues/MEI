import getpass
import time

from spade import quit_spade
from customer import Customer
from manager import Manager
from taxi import Taxi


if __name__ == "__main__":

    # 10 agentes Client, um agente Manager e 5 agentes Taxi. A cada 1 segundo, 10 novos agentes Client deverão ser inicializados.

    manager_jid = "manager@lau-asus"
    passwd = "lau"
    manager = Manager(manager_jid, passwd)
    #buyer.set('seller_jid', seller_jid)
    future = manager.start()
    future.result()
    print("Creating manager...")
    

    taxis = {}
    customers = {}

    print("Creating 5 taxis...")
    for i in range(5):
        taxi_jid = f"taxi{i}@lau-asus"
        taxi = Taxi(taxi_jid, passwd)
        taxis[f'taxi{i}'] = taxi
        taxis[f'taxi{i}'].start()

    while len(customers) < 20:
        print("Creating +10 customers...")
        for i in range(len(customers), len(customers)+10):
            customer_jid = f"customer{i}@lau-asus"
            customer = Customer(customer_jid, passwd)
            customers[f'customer{i}'] = customer
            customers[f'customer{i}'].start()
        time.sleep(1)
    
    while manager.is_alive():
        try:
            time.sleep(1)
        except KeyboardInterrupt:

            for i in range(len(customers)):
                customers[f'customer{i}'].stop()
            for i in range(len(taxis)):
                taxis[f'taxi{i}'].stop()
            
            manager.stop()
            break
    print("Agents finished")
    quit_spade()