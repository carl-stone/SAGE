PYTHON ?= python3
ROBOT_VERSION := 1.9.10
ROBOT_JAR ?= .tools/robot-$(ROBOT_VERSION).jar

.PHONY: setup verify
setup:
	@java -version
	$(PYTHON) -m venv .venv
	.venv/bin/python -m pip install -r requirements-verify.txt
	$(MAKE) "$(ROBOT_JAR)"

$(ROBOT_JAR):
	mkdir -p "$(dir $(ROBOT_JAR))"
	curl --fail --location --retry 2 --connect-timeout 20 \
	  "https://github.com/ontodev/robot/releases/download/v$(ROBOT_VERSION)/robot.jar" \
	  --output "$@.part"
	mv "$@.part" "$@"

verify:
	.venv/bin/python verification/verify.py --robot-jar "$(ROBOT_JAR)"
