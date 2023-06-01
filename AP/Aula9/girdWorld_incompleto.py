#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun May 14 18:52:50 2023

"""

## Reinforcement learning - example with a Grid World


import numpy as np

# global variables
BOARD_ROWS = 3
BOARD_COLS = 4
WIN_STATE = (0, 3)
LOSE_STATE = (1, 3)
START = (2, 0)


class Environment:
    def __init__(self, state=START, deterministic = True):
        self.board = np.zeros([BOARD_ROWS, BOARD_COLS])
        self.board[1, 1] = -1  # forbidden cell
        self.state = state # initial state
        self.isEnd = False
        self.determine = deterministic


    def init(self, initial_state = START):
        self.state = initial_state
        self.isEnd = False

    def set_state(self, state = START):
        self.state = state

    # complete !! - R function
    def giveReward(self):
        if self.state == WIN_STATE:
            return ...
        elif ...

    def isEndFunc(self):
        if (self.state == WIN_STATE) or (self.state == LOSE_STATE):
            self.isEnd = True

    # complete - P function
    def nxtPosition(self, sel_action):
        """
        action: up, down, left, right
        -------------
        0 | 1 | 2| 3|
        1 |
        2 |
        return next position
        """
            
        if self.determine:
            action = sel_action
        else:
            action = self.nxtPositionProb(sel_action)
            
        if action == "up":
            # ...
        elif action == "down":
            # ...
        elif action == "left":
            # ...
        else:
            # ...
             
        # if next state legal
        if (nxtState[0] >= 0) and (nxtState[0] <= (BOARD_ROWS -1)):
            if (nxtState[1] >= 0) and (nxtState[1] <= (BOARD_COLS -1)):
                if nxtState != (1, 1):
                    return nxtState
        return self.state

    def nxtPositionProb(self, action):
        if action == "up":
            return np.random.choice(["up", "left", "right"], p=[0.8, 0.1, 0.1])
        if action == "down":
            return np.random.choice(["down", "left", "right"], p=[0.8, 0.1, 0.1])
        if action == "left":
            return np.random.choice(["left", "up", "down"], p=[0.8, 0.1, 0.1])
        if action == "right":
            return np.random.choice(["right", "up", "down"], p=[0.8, 0.1, 0.1])

    def takeAction(self, action):
        self.state = self.nxtPosition(action)
        return self.state

        

class Agent:

    def __init__(self, environment, deterministic = True, gamma = 0.9):
        self.states = []
        self.actions = ["up", "down", "left", "right"]
        self.lr = 0.2
        self.exp_rate = 0.3  # exploration rate
        self.environment = environment
        self.deterministic = deterministic
        
        if self.deterministic: 
            self.state_values = {}
        else:    
            self.decay_gamma = gamma
            self.Q_values = {}

        for i in range(BOARD_ROWS):
            for j in range(BOARD_COLS):
                if self.deterministic:
                    self.state_values[(i, j)] = 0  # set initial value to 0
                else:
                    self.Q_values[(i, j)] = {}
                    for a in self.actions:
                        self.Q_values[(i, j)][a] = 0 # dict of dict
                        
        if not self.deterministic: print(self.Q_values)

        

    ## complete !!
    def chooseAction(self):
        # choose action with most expected value
        mx_nxt_reward = -10000
        action = ""

        if np.random.uniform(0, 1) <= self.exp_rate: # exploration
            action = np.random.choice(self.actions)
        else:
            # greedy action - explicitly uses Environment !!!
            for a in self.actions:
                if self.deterministic: # if the action is deterministic
                    nxt_reward = None ## replace None by the correct code here ...
                else:
                    nxt_reward = None ## replace None by the correct code here ...
                if nxt_reward >= mx_nxt_reward:
                    action = a
                    mx_nxt_reward = nxt_reward      
        return action


    def init(self):
        self.states = []

    def updateState(self, state, action):
        if self.deterministic:
            self.states.append(state)
        else:
            self.states.append([state, action])

    ## complete code
    def updateValues(self, reward, state):
        # explicitly assign end state to reward values
        self.state_values[state] = reward  # this is optional
        #print("Game End Reward", reward)
        for s in reversed(self.states):
            reward = self.state_values[s] # + ... - complete here
            self.state_values[s] = round(reward, 3)
    
    ## complete code
    def updateQvalues(self, reward, state):
        for a in self.actions:
            self.Q_values[state][a] = reward
        for s in reversed(self.states):
            current_q_value = self.Q_values[s[0]][s[1]]
            reward = current_q_value # + ... - complete here
            self.Q_values[s[0]][s[1]] = round(reward, 3)


    def showValues(self):
        for i in range(0, BOARD_ROWS):
            print('----------------------------------')
            out = '| '
            for j in range(0, BOARD_COLS):
                out += str(self.state_values[(i, j)]).ljust(6) + ' | '
            print(out)
        print('----------------------------------')


class Play:
    def __init__(self, num_episodes = 1000, deterministic = True, gamma = 0.9):
        self.environment = Environment(deterministic)
        self.agent = Agent(self.environment, deterministic, gamma)
        self.num_episodes = num_episodes
        self.deterministic = deterministic
    
    def episode(self):
        while not self.environment.isEnd:
            action = self.agent.chooseAction()
            if not self.deterministic:
                self.agent.updateState(self.environment.state, action)
            nxt_state = self.environment.takeAction(action)
            if self.deterministic:
                self.agent.updateState(self.environment.state, action)
            self.environment.isEndFunc()
        
        if(self.deterministic):
            self.agent.updateValues(self.environment.giveReward(), nxt_state)
        else:
            self.agent.updateQvalues(self.environment.giveReward(), nxt_state)


    def run(self):
        i = 0
        while i < self.num_episodes:
            self.agent.init()
            self.environment.init()
            self.episode()
            i += 1

        if (self.deterministic): 
            print(self.agent.showValues())
        else:
            print(self.agent.Q_values)

def test_deterministic(): # version 1
    # version 1
    play = Play(10000)
    play.run()
        
def test_nondeterministic():
    # version 2
    play = Play(50000, False, 0.9)
    play.run()
           

if __name__ == "__main__":
    test_deterministic()
    #test_nondeterministic()
