build:
	gnatmake $(wildcard src/*.adb) -o bin/main.exe
	rm test.o
	rm test.ali
run:build
	./bin/main.exe