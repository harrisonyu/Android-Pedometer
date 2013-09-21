EXE = test
OBJS = main.o

COMPILER = g++
COMPILER_FLAGS = -c -g -O0 -Wall -Werror
LINKER = g++

test : main.o
	$(LINKER) main.o -o main

# compiling
main.o : main.cpp
	$(COMPILER) $(COMPILER_FLAGS) main.cpp

# clean option
clean :
	-rm -f *.o main