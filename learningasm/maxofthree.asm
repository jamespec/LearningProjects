
    global _maxofthree

    section .text
_maxofthree:
    mov     rax, rdi
    cmp     rax, rsi
    cmovl   rax, rsi
    cmp     rax, rdx
    cmovl   rax, rdx
    ret
