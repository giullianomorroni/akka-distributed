package br.com.hugme.akka.beans.message;

public class JsonMessage implements Message {

	private static final long serialVersionUID = 3137057996197946061L;
	private String jsonObject;

	public JsonMessage(String jsonObject) {
		this.jsonObject = jsonObject;
	}

	public String getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "JsonMessage [" + jsonObject + "]";
	}
	
}
