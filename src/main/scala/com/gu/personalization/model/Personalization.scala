package com.gu.personalization
package model

import com.google.appengine.api.datastore._
import scala.collection.JavaConversions._

object Personalization {

  val datastore = DatastoreServiceFactory.getDatastoreService();


  private def getEntity(userProfileId : String) : Option[Entity] = {
    val q = new Query("Profile");
    q.addFilter("userid", Query.FilterOperator.EQUAL, userProfileId);
    val pq = datastore.prepare(q);
    pq.asIterable.headOption
  }

  def save(userProfileId: String,  terms: String) {
    val entityOption = getEntity(userProfileId)
    if (entityOption.isEmpty){
      val profile = new Entity("Profile")
      profile.setProperty("userid", userProfileId)
      profile.setProperty("terms", terms)
      datastore.put(profile)
    }else{
      val profile = entityOption.get
      val currentTerms = profile.getProperty("terms")
      
      val builder = new StringBuilder()
      builder.append(currentTerms).append(",").append(terms)
      profile.setProperty("terms", builder.toString())
      datastore.put(profile)
    }
  }

  def get(userProfileId: String) : String = {
    getEntity(userProfileId).map(_.getProperty("terms").asInstanceOf[String]).getOrElse("")
  }


}
