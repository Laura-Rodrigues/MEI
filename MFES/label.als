sig Item {
	var labels : set Label
}
sig Label {}

fun find [l : Label] : set Item { 
	labels.l 
}

pred affix [i : Item, l : Label] {
	i not in find[l]
	labels' = labels + i->l
}

pred detach [i : Item, l : Label] {
	i in find[l]
	labels' = labels - i->l
}

pred clear [i : Item] {
	labels' = labels - i->Label
}

pred stutter {
	labels' = labels
}

fact Behavior {
	// Initial state
	no labels
	// Transitions
	always {
		(some i : Item, l : Label | affix[i,l] or detach[i,l])
		or 
		(some i : Item | clear[i])
		or
		stutter 
	}
}

run Scenario1 {
	some i : Item, disj l,m : Label {
		affix[i,l];affix[i,m];clear[i]
	}
} expect 1

run Scenario2 {
	some i : Item, l : Label {
		affix[i,l];affix[i,l]
	}
} expect 0

assert OperationalPrinciple {
	all i : Item, l : Label {
		// after affix(i,l) and no detach(i,l), i in find(l)
		always (affix[i,l] implies after ((detach[i,l] or clear[i]) releases i in find[l]))
		// if no affix(i,l), or detach(i,l), i not in find(l) 
		all i : Item, l : Label | affix[i,l] releases i not in find[l]
		always ((clear[i] or detach[i,l]) implies after (affix[i,l] releases i not in find[l]))
	}
}

check OperationalPrinciple
