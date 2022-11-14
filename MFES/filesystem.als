abstract sig Object {}
sig Dir extends Object {
	entries : set Entry
}
sig File extends Object {}
one sig Root extends Dir {}
sig Entry {
	object : one Object,
	name   : one Name
}
sig Name {}

fact {
	// Every entry belongs to one dir
	entries in Dir one -> Entry
}

fact {
	// All objects except the root are contained in at least one entry
	all o : Object - Root | some object.o
	no object.Root

	// All directories are contained in at most one entry
	all d : Dir | lone object.d

	// Different entries in a directory must have different names
	all d : Dir, n : Name | lone (d.entries & name.n)

	// A directory cannot be contained in itself
	all d : Dir | d not in d.^(entries.object)
}


run {} for 4 but 3 Name


assert NoPartitions {
	// All objects are reachable from the root
	Object in Root.*(entries.object)
}

check NoPartitions
