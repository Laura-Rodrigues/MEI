


void swap(int t[],int i,int j) 
  //@ requires 0 <= i < length(t) ;
  //@ requires 0 <= j < length(t) ;
  //@ ensures t[i]==old(t[j]) ;
  //@ ensures t[j]==old(t[i]) ;
  //@ ensures forall k. k!=i && k!=j -> t[k]==old(t[k]) ;
{ 
    int tmp = t[i];
    t[i] = t[j];
    t[j] = tmp;
}


int select(int u[], int i, int j) 
  //@ requires 0 <= i <= j < length(u) ; 
  //@ ensures i <= result <= j ;
  //@ ensures forall k. i <= k <= j -> u[result] <= u[k];
  //@ ensures forall k. i <= k <= j -> u[k] == old(u[k]);
{
 int m = i; int k = i+1; 
  while (k <= j) {
    //@ invariant 
    //@ i <= m < k <= j+1 && 
    //@    (forall a. i <= a < k -> u[m] <= u[a]);
    //@ variant 
    //@    j-k;
    if (u[k] < u[m]) { m = k; }
    k++;
  }

  return m;
}

void selectionSort (int v[], int N) {
  //@ requires 0 < N <= length(v) ;
  //@ ensures forall a, b. 0 <= a < b < N -> v[a] <= v[b] ;
  
  int i; int next;
  for (i=0; i < N-1; i++) {    
    //@ invariant 
    //@   0 <= i <= N-1 && 
    //@   (forall a, b. 0 <= a < b < i -> v[a] <= v[b]) && 
    //@   (forall a, b. 0 <= a < i <= b < N -> v[a] <= v[b]) ;
    //@ variant 
    //@    N-i;
    next = select(v, i, N-1);
    if (next != i) swap (v, i, next);
  }
}

