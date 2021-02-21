
    global _main
    extern _printf
    extern _puts
    extern _atoi

    section .text

_main:
    push r12        ; Required to restore these on exit
    push r13
    push r14
    ; Three pushes aligns our stack for calls

    cmp rdi, 3      ; 2 args required
    jne errorBadArgumentCount

    mov r12, rsi    ; rsi is argv

; We will use:
; r13d (32bits) to hold the exponent
; r14d (32bits) to hold the base

    mov rdi, [r12+16]   ; argv[2] - Y
    call _atoi          ; convert to integer, result in eax
    cmp eax, 0
    jl  errorNegativeExponent
    mov r13d, eax

    mov rdi, [r12+8]    ; argv[1] - X
    call _atoi          ; convert to integer, result in eax
    mov r14d, eax

; Keep running total in eax, start with 1 and loop multiplying by base
; Upgraded for 64 bit multiply
    mov rax, 1      ; was mox eax, 1

; Loop while power (r13d) is not zero, mul by base and dec r13d
check:
;    test r13d, r13d
    cmp r13d, 0
    jz printout
    imul r14        ; was imul eax, r14d
                    ; single operand imul with a 64bit operand will perform 64 bit mult with rax
    dec r13d
    jmp check

printout:
    mov rdi, answer_format
    mov rsi, rax        ; was movsxd rsi, eax
    xor rax, rax       ; set next register to zero, varargs requirement
    call _printf
    jmp done

errorBadArgumentCount:
    mov rdi, badArgumentCount
    call _puts
    jmp done

errorNegativeExponent:
    mov rdi, negativeExponent
    call _puts

done:
    pop r14
    pop r13
    pop r12
    ret

answer_format:
    db  "%lld", 10, 0
badArgumentCount:
    db  "Requires exactly two arguments", 0     ; puts adds nl
negativeExponent:
    db  "The exponent may not be negative", 0   ; puts adds nl
