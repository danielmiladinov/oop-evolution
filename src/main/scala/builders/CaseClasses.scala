package builders

object CaseClasses {

  case class Vehicle(name: String, numWheels: Int = 4, maybeNumCylinders: Option[Int] = None) {
    val vroom: String = {
      val baseVroom = s"Here goes $name with its $numWheels wheels"
      maybeNumCylinders.fold(baseVroom) { numCylinders =>
        baseVroom + s" and its $numCylinders cylinders"
      }
    }
  }

  // println(Vehicle("Cadillac").vroom)
  // println(Vehicle("Cadillac", 3).vroom)
  // println(Vehicle(name = "Cadillac", maybeNumCylinders = Some(4)).vroom)
  // println(Vehicle("Cadillac").copy(numWheels = 3, maybeNumCylinders = Some(4)).vroom)



  case class Owner(name: String, age: Int, vehicles: Vector[Vehicle])


  def main(arg: Array[String]): Unit = {
    import monocle.macros.GenLens
    import monocle.function.all._
    import monocle.Traversal
    import monocle.unsafe.UnsafeSelect
    val vehicles = GenLens[Owner](_.vehicles)
    val teslaFilter = UnsafeSelect.unsafeSelect[Vehicle](_.name == "Tesla")
    val numCylinders = GenLens[Vehicle](_.maybeNumCylinders)
    val optCylinderOfTesla = 
      vehicles composeTraversal each composePrism teslaFilter composeLens numCylinders
    val original = Owner(name = "Rich Guy",
      age = 43,
      vehicles = Vector(
        Vehicle("Cadillac"),
        Vehicle("Tesla")
      ))
    println(s"Original owner: $original");
    println(s"Can find tesla car with cylinder field: ${!optCylinderOfTesla.isEmpty(original)}")
    println(s"Suped up tesla in owner: ${(optCylinderOfTesla set Some(8))(original)}")
  }


}
