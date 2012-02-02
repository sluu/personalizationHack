package com.gu.personalization
package api


object Api {

  object Personalisation {
      def save(userProfileId: String, params: Map[String, String]): model.Response = {
        val terms = params.getOrElse("terms", "").trim.split("|")
        ConnectionManager.session {
          repository.Personalization.save(userProfileId, terms)

          model.Response("ok", profile.id.toString)
        }
      }
  }

}