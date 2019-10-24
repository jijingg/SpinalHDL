package spinal.lib.bus.regif

import spinal.core._
import spinal.lib.bus.amba3.apb.Apb3
import spinal.lib.bus.misc.SizeMapping

case class Apb3BusInterface(bus: Apb3, sizeMap: SizeMapping, selId: Int = 0, readSync: Boolean = true) extends BusIf{

  val readError = Bool().setAsReg()
  val readData  = Bits(bus.config.dataWidth bits).setAsReg()

  if(readSync) {
    readError.setAsReg()
    readData.setAsReg()
  } else {
    readError := False
    readData  := 0
  }

  bus.PREADY := True
  bus.PRDATA := readData
  if(bus.config.useSlaveError) bus.PSLVERROR := readError

  val askWrite  = (bus.PSEL(selId) && bus.PENABLE && bus.PWRITE).allowPruning()
  val askRead   = (bus.PSEL(selId) && bus.PENABLE && !bus.PWRITE).allowPruning()
  val doWrite   = (bus.PSEL(selId) && bus.PENABLE && bus.PREADY &&  bus.PWRITE).allowPruning()
  val doRead    = (bus.PSEL(selId) && bus.PENABLE && bus.PREADY && !bus.PWRITE).allowPruning()
  val writeData = bus.PWDATA

  override def readAddress()  = bus.PADDR
  override def writeAddress() = bus.PADDR

  override def readHalt()  = bus.PREADY := False
  override def writeHalt() = bus.PREADY := False

  override def busDataWidth   = bus.config.dataWidth
}