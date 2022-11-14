	.file	"mmult.c"
	.text
	.globl	A
	.bss
	.align 8
	.type	A, @object
	.size	A, 8
A:
	.zero	8
	.globl	B
	.align 8
	.type	B, @object
	.size	B, 8
B:
	.zero	8
	.globl	C
	.align 8
	.type	C, @object
	.size	C, 8
C:
	.zero	8
	.text
	.globl	alloc
	.type	alloc, @function
alloc:
.LFB6:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	$2097152, %edi
	call	malloc@PLT
	movq	%rax, A(%rip)
	movl	$2097152, %edi
	call	malloc@PLT
	movq	%rax, B(%rip)
	movl	$2097152, %edi
	call	malloc@PLT
	movq	%rax, C(%rip)
	nop
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE6:
	.size	alloc, .-alloc
	.globl	init
	.type	init, @function
init:
.LFB7:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$16, %rsp
	movl	$0, -8(%rbp)
	jmp	.L3
.L6:
	movl	$0, -4(%rbp)
	jmp	.L4
.L5:
	call	rand@PLT
	movl	%eax, %edx
	movq	A(%rip), %rcx
	movl	-8(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %esi
	movl	-4(%rbp), %eax
	addl	%esi, %eax
	cltq
	salq	$3, %rax
	addq	%rcx, %rax
	pxor	%xmm0, %xmm0
	cvtsi2sdl	%edx, %xmm0
	movsd	%xmm0, (%rax)
	call	rand@PLT
	movl	%eax, %edx
	movq	B(%rip), %rcx
	movl	-8(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %esi
	movl	-4(%rbp), %eax
	addl	%esi, %eax
	cltq
	salq	$3, %rax
	addq	%rcx, %rax
	pxor	%xmm0, %xmm0
	cvtsi2sdl	%edx, %xmm0
	movsd	%xmm0, (%rax)
	movq	C(%rip), %rdx
	movl	-8(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %ecx
	movl	-4(%rbp), %eax
	addl	%ecx, %eax
	cltq
	salq	$3, %rax
	addq	%rdx, %rax
	pxor	%xmm0, %xmm0
	movsd	%xmm0, (%rax)
	addl	$1, -4(%rbp)
.L4:
	cmpl	$511, -4(%rbp)
	jle	.L5
	addl	$1, -8(%rbp)
.L3:
	cmpl	$511, -8(%rbp)
	jle	.L6
	nop
	nop
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE7:
	.size	init, .-init
	.globl	mmult
	.type	mmult, @function
mmult:
.LFB8:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	$0, -12(%rbp)
	jmp	.L8
.L13:
	movl	$0, -8(%rbp)
	jmp	.L9
.L12:
	movl	$0, -4(%rbp)
	jmp	.L10
.L11:
	movq	C(%rip), %rdx
	movl	-12(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %ecx
	movl	-4(%rbp), %eax
	addl	%ecx, %eax
	cltq
	salq	$3, %rax
	addq	%rdx, %rax
	movsd	(%rax), %xmm1
	movq	A(%rip), %rdx
	movl	-12(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %ecx
	movl	-8(%rbp), %eax
	addl	%ecx, %eax
	cltq
	salq	$3, %rax
	addq	%rdx, %rax
	movsd	(%rax), %xmm2
	movq	B(%rip), %rdx
	movl	-8(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %ecx
	movl	-4(%rbp), %eax
	addl	%ecx, %eax
	cltq
	salq	$3, %rax
	addq	%rdx, %rax
	movsd	(%rax), %xmm0
	mulsd	%xmm2, %xmm0
	movq	C(%rip), %rdx
	movl	-12(%rbp), %eax
	sall	$9, %eax
	movl	%eax, %ecx
	movl	-4(%rbp), %eax
	addl	%ecx, %eax
	cltq
	salq	$3, %rax
	addq	%rdx, %rax
	addsd	%xmm1, %xmm0
	movsd	%xmm0, (%rax)
	addl	$1, -4(%rbp)
.L10:
	cmpl	$511, -4(%rbp)
	jle	.L11
	addl	$1, -8(%rbp)
.L9:
	cmpl	$511, -8(%rbp)
	jle	.L12
	addl	$1, -12(%rbp)
.L8:
	cmpl	$511, -12(%rbp)
	jle	.L13
	nop
	nop
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE8:
	.size	mmult, .-mmult
	.section	.rodata
.LC1:
	.string	"%f\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB9:
	.cfi_startproc
	endbr64
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	$0, %eax
	call	alloc
	movl	$0, %eax
	call	init
	movl	$0, %eax
	call	mmult
	movq	C(%rip), %rax
	addq	$2088, %rax
	movq	(%rax), %rax
	movq	%rax, %xmm0
	leaq	.LC1(%rip), %rax
	movq	%rax, %rdi
	movl	$1, %eax
	call	printf@PLT
	movl	$0, %eax
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE9:
	.size	main, .-main
	.ident	"GCC: (Ubuntu 11.2.0-19ubuntu1) 11.2.0"
	.section	.note.GNU-stack,"",@progbits
	.section	.note.gnu.property,"a"
	.align 8
	.long	1f - 0f
	.long	4f - 1f
	.long	5
0:
	.string	"GNU"
1:
	.align 8
	.long	0xc0000002
	.long	3f - 2f
2:
	.long	0x3
3:
	.align 8
4:
