NAME = rtcrm
JAR = target/$(NAME).jar

all: clean $(JAR)

.PHONE: clean
clean:
	rm -rf target/

$(JAR):
	./mvnw -U clean package
