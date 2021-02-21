
all: hello stars holamundo maxofthree echoargs power

clean:
	rm *.o hello stars holamundo

hello: hello.o
	ld -e start -static hello.o -o hello


hello.o: hello.asm
	nasm -fmacho64 hello.asm


stars: stars.o
	ld -e start -static stars.o -o stars


stars.o: stars.asm
	nasm -fmacho64 stars.asm


holamundo: holamundo.o
	gcc holamundo.o -o holamundo -Wl,-no_pie

holamundo.o: holamundo.asm
	nasm -fmacho64	holamundo.asm

maxofthree: maxofthree.o callmaxofthree.o
	gcc maxofthree.o callmaxofthree.o -o maxofthree

maxofthree.o: maxofthree.asm
	nasm -fmacho64 maxofthree.asm

callmaxofthree.o: callmaxofthree.c
	gcc -c callmaxofthree.c

echoargs: echoargs.o
	gcc echoargs.o -o echoargs

echoargs.o: echoargs.asm
	nasm -fmacho64 echoargs.asm

power: power.o
	gcc power.o -o power -Wl,-no_pie

power.o: power.asm
	nasm -fmacho64 power.asm
