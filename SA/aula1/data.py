def save (timestamp,event,value):
    f = open('logger.txt', 'a')
    f.write(timestamp + '|' + event + '|' + value + '\n')
    f.close()