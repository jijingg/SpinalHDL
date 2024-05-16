package spinal.lib.bus.regif.BusIfAdapter

import spinal.core._
import spinal.lib.bus.localbus.lbus
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.regif._

case class lbusBusInterface(bus: lbus, sizeMap: SizeMapping)(implicit moduleName: ClassName) extends BusIf{
  override val busDataWidth: Int = bus.c.dw
  override val busAddrWidth: Int = bus.c.aw
  override val withStrb: Boolean = true
  override def getModuleName = moduleName.name

  val readError: Bool = Bool()
  val readData: Bits  = Bits(busDataWidth bits)
  val reg_rderr: Bool = Reg(Bool(), init = False)
  val reg_rdata: Bits = Reg(Bits(busDataWidth bits), init = defualtReadBits)

  val wstrb: Bits  = withStrb generate(Bits(strbWidth bit))
  val wmask: Bits  = withStrb generate(Bits(busDataWidth bit))
  val wmaskn: Bits = withStrb generate(Bits(busDataWidth bit))
  withStrb generate(wstrb := bus.strb)

  bus.rdy := True
  bus.rdat := readData

  val askWrite  = bus.ce && bus.wr
  val askRead   = bus.ce && !bus.wr
  val doWrite   = (askWrite && bus.rdy).allowPruning()
  val doRead    = (askRead  && bus.rdy).allowPruning()
  val writeData = bus.wdat

  initStrbMasks()

  override def readAddress()  = if(withStrb) (bus.addr.drop(underbitWidth) ## B(0, underbitWidth bit)).asUInt else bus.addr
  override def writeAddress() = bus.addr

  override def readHalt()  = bus.rdy := False
  override def writeHalt() = bus.rdy := False

  override val regPre: String = ""
}
