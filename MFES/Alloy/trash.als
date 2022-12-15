module Trash [Item]

var sig Accessible in Item {}
var sig Trashed in Item {}

pred create[i : Item] {
	historically i not in Accessible
	Accessible' = Accessible + i
	Trashed' = Trashed 
}

pred delete[i : Item] {
	i in Accessible
	Accessible' = Accessible - i
	Trashed' = Trashed + i
}

pred restore[i : Item] {
	i in Trashed
	Accessible' = Accessible + i
	Trashed' = Trashed - i
}

pred empty {
	some Trashed
	no Trashed'
	Accessible' = Accessible
}

pred stutter {
	Accessible' = Accessible
	Trashed' = Trashed
}

fact Init {
	no Accessible
	no Trashed
}

fact Traces {
	always (stutter or empty or some i : Item | create[i] or delete[i] or restore[i])
}

run Scenario1 {
	some i : Item {
		create[i]; delete[i]; restore[i]; delete[i]; empty
	}
} expect 1

run Scenario2 {
	some disj i,j: Item {
		create[i]; delete[j]
	}
} expect 0

run Scenario3 {
	eventually empty
} for 1 Item expect 1

assert OperationalPrinciple {
	all x : Item | always {
		// after delete(x), can restore(x) and then x in accessible
		delete[x] implies after (x in Trashed and (restore[x] implies after x in Accessible))
		// after delete(x), can empty() and then x not in accessible or trashed
		delete[x] implies after (some Trashed and (empty implies after x not in Trashed+Accessible))
	}
}

check OperationalPrinciple for 4 Item, 20 steps

assert Properties {
	// No item can simultaneously be accessible and trashed
	always no Accessible & Trashed
	// A restore is only possible after a delete
	all x : Item | always (restore[x] implies once delete[x])
	// If all items are in the trash and the trash is emptied no more items will exist
	always (Item in Trashed and empty implies always no Accessible)
	// After deleting, an item stays trashed until an empty or a restore
	all x : Item | always (delete[x] implies after ((empty or restore[x]) releases x in Trashed))
	// A restore undos a delete
	all x : Item | always ((delete[x];restore[x]) implies Accessible'' = Accessible and Trashed'' = Trashed)
}

check Properties
