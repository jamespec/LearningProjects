
    global  _main
    extern  _puts

    section .text
_main:
    mov     r12, rdi
loop:
    push    rsi
    sub     rsp, 16          ; Align Stack for call to _puts

    mov     rdi, [rsi]
    call    _puts

    add     rsp, 16
    pop     rsi

    add     rsi, 8
    dec     r12
    jnz     loop

    ret
