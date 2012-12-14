class FeedbackType {
	public String name;

	public FeedbackType(String n) {
		this.name = n;
	}

	public String toString() {
		return name;
	}
}

class FeedbackReturn {
	public String message;
	public boolean error = false;
}

class FeedbackClient {

	public FeedbackReturn submitFeedback(JsonObject params) throws IOException {
		FeedbackReturn r = new FeedbackReturn();
		r.error = true;
		r.message = "Testing";
		URL url = new URL("https://www.appygram.com/appygrams");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		String input = params.toString();
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		r.error = conn.getResponseCode() != 200;

		String output;
		while ((output = br.readLine()) != null) {
			r.message += output;
		}

		conn.disconnect();

		return r;
	}

	public ArrayList<FeedbackType> getFeedbackTypes(String key)
			throws IOException {
		ArrayList<FeedbackType> types = new ArrayList<FeedbackType>();
		URL url = new URL("https://www.appygram.com/api/topics/" + key);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		String result = readStream(con.getInputStream());
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(result);
		JsonObject jo = je.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
			JsonObject type = entry.getValue().getAsJsonObject();
			types.add(new FeedbackType(type.get("name").getAsString()));
		}
		con.disconnect();
		return types;
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;
		String response = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response += line;
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}
}

