
    ; This module defines _main and uses _
    ; getchar and _puts
    ; Link with gcc to make the standard C library avilable
    global  _main
    extern  _getchar
    extern  _putchar
    extern  _puts
    extern  _printf

    section .data
dummy:          dq      0
prompt:         db      "Enter two characters: ", 0
firstinitial:   db      "J"
lastinitial:    db      "P"
fmt:            db      "Number of matching bits - %ld", 10, 0
fmtbin:         db      "Character: 0x%X", 10, 0
here:           db      "Here!", 0
quit:           db      "quit", 10, 0

    section .bss
buffer2:        resb    32
binstring:      resb    24
first_entry:    resb    1
second_entry:   resb    1

; Some notes on calling C routines:
; rdi, rsi, rdx, rcx, r8, r9 - Integer/pointer params in order (there are more if necessary)
; The stack is used on occasion when the parameter types aren't 8 bytes or more than 8.
; The return value is put in RAX

; "call" and "ret" use a stack to store the return address (and some parameters)
; The convention is that the stack needs to always have a multiple of 16 bytes.
; If calling a C routine that doesn't use the stack for parameters, the only thing added
; is the return address.  This is typical when only a few parameters are passed.
; This is only 8 bytes so an extra 8 bytes needs to be added to keep the convention.
; These bytes are ignored and only there for the 16 byte requirement.
; NASM sets up the stack memory automatically.
; The RSP register is used to point to tbe next memory address (top of the stack)
; Push and Pop manage the RSP register.

    section .text
_main:
    ; Prompt  for two chars:
    push    rbx                 ; The stack needs to have an extra 8 bytes, push them here.
                                ; (don't care about the value)

top:
    mov     rdi, prompt         ; First parameter is the address of the string.
    xor     rax, rax            ; No extra parameters (printf supports variable number)
    call    _printf

    mov     rdi, buffer2
    mov     rsi, 30
    call    fetchline

    mov     rdi, buffer2
    mov     rsi, quit
    call    stricmp

    cmp     rax, 0
    je      .done

    mov     r13, buffer2
    xor     rdi, rdi
    mov     dil, [r13]         ; Move first char to RDI
    mov     rsi, binstring
    call    outbinary
    mov     rdi, binstring
    call    _puts

    mov     dil, [r13+1]         ; Move first char to RDI
    mov     rsi, binstring
    call    outbinary
    mov     rdi, binstring
    call    _puts

    mov     r13, buffer2
    mov     DIL, [r13]         ; Move first char to RDI
    mov     SIL, [r13+1]       ; Move second char to RSI
    call    comparebits

    mov     rdi, fmt
    mov     rsi, rax
    xor     rax, rax
    call    _printf

    jmp     top

.done:
    pop     rbx
    ret


; ============================================
; comparebits
;     Compare the bits of two characters.
;     First character parameter is in RDI
;     Second character parameter is in RSI
;     Number of common bits returned in RAX
; ============================================
comparebits:
    xor     rdi, rsi            ;
    not     rdi                 ; 1's when the bits are the same

    ; Count the 1's
    mov     rax, 0
    mov     rbx, 0x80           ; Start with a bit mask on the 8th bit
more_bits_to_check:
    mov     rcx, rdi
    and     rcx, rbx
    cmp     rcx, 0
    jz      nope
    inc     rax
nope:
    shr     rbx, 1
    cmp     rbx, 0
    jne     more_bits_to_check      ; end of loop
    ret

; stricmp
; Compare two string without regard to case
; String one in RDI, String two in RDI
; returns <0 if string 1 is less than 2
; returns >0 if string 1 is greater then 2
; returns 0 is equal
stricmp:
    push    r14

    xor     r14, r14    ; index of next char to compare - zero
.loop:
    mov     AL, [rdi+r14]
    and     rax, 0b11011111 ; toupper
    mov     DL, [rsi+r14]
    and     rdx, 0b11011111 ; toupper
    inc     r14
    sub     rax, rdx    ; Subtract the full 64bits
    cmp     rax, 0      ; Compare the difference to zero
    jne     .done
    cmp     DL, 0      ; Equal but are they both null terminator?
    je      .done
    jmp     .loop
.done:
    pop     r14
    ret


; Routine getline
; Parameters: rdi - input buffer to place characters
; Reads chars up to a newline and places them in buffer
; rsi is the maximum number of character to read (buffer size)
fetchline:
    push    r12                 ; Save r12, r13, r14
    push    r13
    push    r14

    xor     r12, r12            ; r12 used to index into array buffer
    mov     r13, rdi            ; copy rdi to r13, can't use rdi, _getchar will squash it.
    mov     r14, rsi            ; copy rsi to r14, can't use rsi, _getchar will squash it.
.nextchar:
    call    _getchar
    mov     [r13+r12], AL       ; Use AL as this is the right most byte of the RAX register, size of 1 byte
    inc     r12
    cmp     AL, 10              ; test for \n
    je      .done
    cmp     r12, r14            ; Did we read the max number of chars?
    je      .done
    jmp     .nextchar
.done:
    mov     [r12+r13], byte 0   ; Add the null terminator
    mov     rax, r12            ; Move the number of characters read to RAX, return

    pop     r14                 ; Restore registers
    pop     r13
    pop     r12
    ret


; Routine: outbinary
; Parameters:
; rdi character to print as binary
; rsi buffer for formatted string
outbinary:
    push    r12                     ; Save r12,r13,r14
    push    r13
    push    r14

    xor     r12, r12                ; r12 will be used to index into array
    mov     [rsi+r12], byte '0'     ; put a '0' as first character
    inc     r12                     ; increment to next character
    mov     [rsi+r12], byte 'x'     ; put a 'x' next
    inc     r12

    mov     r13, 0x80               ; set up the mask as 0x80
.next:
    mov     r14, rdi                ; copy char to r14
    and     r14, r13                ; mask off the one bit
    cmp     r14, 0                  ; check for a zero
    je      .zero
.one:
    mov     [rsi+r12], byte '1'     ; Add a '1' to string
    inc     r12
    jmp     .bottom
.zero:
    mov     [rsi+r12], byte '0'     ; Add a '0' to string
    inc     r12
.bottom:
    shr     r13, 1                  ; shift the mask to the right
    cmp     r13, 0                  ; see if mask is zero
    jne     .next                   ; if not zero there are more bits to check

    mov     [rsi+r12], byte 0       ; Add null terminator

    pop     r14
    pop     r13
    pop     r12
    ret
