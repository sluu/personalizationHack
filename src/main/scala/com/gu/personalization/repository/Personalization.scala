package com.gu.personalization
package repository


object Personalization {

    dev save(userProfileId: String,  terms : List[String]) = {
      val newPersonalization = new repository.Personalization(userId=userProfileId, terms )
      newPersonalization.save

    }
}

case class Personalization(val userId : String,  val terms : List[String]) {

  def save = //save to somewhere

}