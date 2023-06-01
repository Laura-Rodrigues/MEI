from pynput import mouse
import data
import datetime

def get_current_time():
    now = datetime.datetime.now()
    return now.strftime("%Y-%m-%d %H:%M:%S")

#detect mouse movement
def on_move(x, y):
    #print('Pointer moved to {0}'.format((x, y)))
    timestamp = get_current_time()
    event = "MouseMovement"
    value = format((x, y))
    data.save(timestamp,event,value)

#detect mouse scroll
def on_scroll(x, y, dx, dy):
    #print('Mouse scrolled at ({0}, {1})({2}, {3})'.format(x, y, dx, dy))
    timestamp = get_current_time()
    event = "MouseScroll"
    value = format(x, y, dx, dy)
    data.save(timestamp,event,value)

#detect mouse click
def on_click(x, y, button, pressed):
    if pressed:
        #print('Mouse clicked at ({0}, {1}) with {2}'.format(x, y, button.name))
        timestamp = get_current_time()
        event = "MouseClicked"
        value = format(button.name)
        data.save(timestamp,event,value)
    if button == mouse.Button.middle:
        #stop listener
        print('Gracefully Stopping!')
        return False
    
#collecting events
with mouse.Listener(
    on_move=on_move,
    on_click=on_click,
    on_scroll=on_scroll) as listener:
    listener.join()