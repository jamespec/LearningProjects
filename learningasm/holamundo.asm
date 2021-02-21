
    global  _main
    extern  _puts

    section .text
_main:
    push    rbx
    mov     rdi, message
    call    _puts
    pop     rbx
    ret

    section .data
message:
    db      "Hola Mundo", 0
