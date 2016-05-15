package org.gsf.data

import org.scalatest.FlatSpec
import org.scalatest.ShouldMatchers
/**
  * Created by Scott on 5/11/16.
  */
class DAOtest extends FlatSpec with ShouldMatchers{
  "A Data Access object" should "be able to store data into mongodb" in{
    val client = DAO.mongo
    client
    .store(10010L,234.5,110.5,110.5,"fuck") shouldBe true
  }
}
