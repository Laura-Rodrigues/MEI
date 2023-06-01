import datetime
import data
from pynput import keyboard
from Adafruit_IO import Client, Data

aio = Client('YOUR ADAFRUIT IO USERNAME', 'YOUR ADAFRUIT IO KEY')
# acabar de implementar adafruit ou firebase


# Create a data item with value 10 in the 'Test' feed.
data = Data(value=10)
aio.create_data('Test', data)


Total_pressed = {}


def get_current_time():
    now = datetime.datetime.now()
    return now.strftime("%Y-%m-%d %H:%M:%S")

#detect key press
def on_press(key):
    try:
        #print('alphanumeric key {0} pressed'.format(key.char))
        timestamp = get_current_time()
        event = "KeyPressed"
        value = format(key.char)
        data.save(timestamp,event,value)
        if key.char in Total_pressed:
            Total_pressed[key.char] += 1
        else:
            Total_pressed[key.char] = 1
    except:
        #print('special key {0} pressed'.format(key))
        timestamp = get_current_time()
        event = "KeyPressed"
        value = format(key)
        data.save(timestamp,event,value)
        if key in Total_pressed:
            Total_pressed[key] += 1
        else:
            Total_pressed[key] = 1



#detect key releases
def on_release(key):
    #print('{0} released'.format(key))
    timestamp = get_current_time()
    event = "KeyReleased"
    value = format(key)
    data.save(timestamp,event,value)
    if key == keyboard.Key.esc:
        #stop listener
        print('Gracefully Stopping!')
        print(f"Total pressed characters: {len(Total_pressed)}")
        print(f"Top 5 pressed characters: {sorted(Total_pressed, key=Total_pressed.get, reverse=True)[:5]}")
        print(f"Total of times each character was pressed: {Total_pressed}")

        return False

#collecting events
with keyboard.Listener(
    on_press=on_press,
    on_release=on_release) as listener:
    listener.join()