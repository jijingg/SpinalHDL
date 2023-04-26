apb4:
	sbt "tester/test:runMain  spinal.tester.generator.regif.Apb4test "
gen:
	sbt "tester/test:runMain  spinal.tester.generator.regif.playregif "
regif:
	sbt "tester/testOnly spinal.tester.scalatest.RegIfBasicAccessTester"
axi4:
	sbt "tester/testOnly spinal.tester.scalatest.RegIfAxiLite4Tester"
genrtl:
	sbt "tester/testOnly spinal.tester.scalatest.RegIfBasicRtlGenerater"
strb:
	sbt "tester/testOnly spinal.tester.scalatest.RegIfStrbTester "
