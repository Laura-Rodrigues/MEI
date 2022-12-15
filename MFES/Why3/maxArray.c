
int maxarray(int u[], int size) {
  //@ requires 0 < size < length(u) ;
  //@ ensures 0 <= result < size ;
  //@ ensures forall a. 0 <= a < size -> u[a] <= u[result];
  
  int i = 1; int m = 0;
  while (i < size) {
    //@ invariant 
    //@ 0 <= m < i <= size && 
    //@    (forall a. 0 <= a < i -> u[a] <= u[m]);
    //@ variant 
    //@    size-i;
    if (u[i] > u[m]) { m = i; }
    i = i+1;
  }

  return m;
}

