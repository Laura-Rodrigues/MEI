#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>


#define size 512


double *A, *B, *C;
double *pA, *pB, *pC;

void alloc() {
    if (myrank==0){
        A = (double *) malloc(size*size*sizeof(double));
        //B = (double *) malloc(size*size*sizeof(double));
        C = (double *) malloc(size*size*sizeof(double));  
    }
        pA = (double *) malloc(size*(size/nprocs)*sizeof(double));
        B = (double *) malloc(size*size*sizeof(double));
        pC = (double *) malloc(size*(size/nprocs)*sizeof(double));  
    
}

void init() {
    for(int i=0; i<size; i++) {
        for(int j=0; j<size; j++) {
            A[i*size+j] = rand();
            B[i*size+j] = rand();
            //C[i*size+j] = 0;
        }
    }
}

void mmult() {
    MPI_Scatter(A, size*(size/nprocs), MPI_DOUBLE, pA, size*(size/nprocs), MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(B, size*size, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    for(int i=0; i<size/nprocs; i++) {
    	for(int k=0; k<size; k++) {
		    for(int j=0; j<size; j++) {
            	pC[i*size+j] += pA[i*size+k] * B[k*size+j];
            }
        }
    }

    MPI_Gather(pC, size*(size/nprocs), MPI_DOUBLE, C, size*(size/nprocs), MPI_DOUBLE, 0, MPI_COMM_WORLD);
}

//falta atualizar esta
int main() {
    alloc();
    init();
    mmult();
    printf("%f\n", C[size/2+5]);
}