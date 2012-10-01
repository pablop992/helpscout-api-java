package net.helpscout.api;

import com.google.gson.*;
import net.helpscout.api.adapters.StatusAdapter;
import net.helpscout.api.adapters.ThreadStateAdapter;
import net.helpscout.api.adapters.ThreadsAdapater;
import net.helpscout.api.cbo.Status;
import net.helpscout.api.cbo.ThreadState;
import net.helpscout.api.exception.*;
import net.helpscout.api.model.*;
import net.helpscout.api.model.thread.LineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ApiClient {

	final static Logger log = LoggerFactory.getLogger(ApiClient.class);

	// private final static String BASE_URL = "https://api.helpscout.net/v1/";
	private final static String BASE_URL = "http://localhost:9000/v1/";
	private final static String METHOD_GET = "GET";
	private final static String METHOD_POST = "POST";
	private final static String METHOD_PUT = "PUT";
	private final static String METHOD_DELETE = "DELETE";

	private String apiKey = "";

	private static ApiClient instance = null;

	private ApiClient() {
	}

	public synchronized static ApiClient getInstance() {
		if (instance == null) {
			synchronized (BASE_URL) {
				if (instance == null) {
					instance = new ApiClient();
				}
			}
		}
		return instance;
	}

	public void setKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Mailbox getMailbox(Integer mailboxID) throws ApiException {
		return (Mailbox)getItem("mailboxes/" + mailboxID + ".json", Mailbox.class, 200);
	}

	public Mailbox getMailbox(Integer mailboxID, List<String> fields) throws ApiException {
		if (mailboxID == null || mailboxID < 1) {
			throw new ApiException("Invalid mailboxId in getMailbox");
		}
		String url = setFields("mailboxes/" + mailboxID + ".json", fields);
		return (Mailbox)getItem(url, Mailbox.class, 200);
	}

	public Page getMailboxes() throws ApiException {
		return getPage("mailboxes.json", Mailbox.class, 200);
	}

	public Page getMailboxes(List<String> fields) throws ApiException {
		String url = setFields("mailboxes.json", fields);
		return getPage(url, Mailbox.class, 200);
	}

	public Page getFolders(Integer mailboxId) throws ApiException {
		return getPage("mailboxes/" + mailboxId + "/folders.json", Folder.class, 200);
	}

	public Page getFolders(Integer mailboxId, List<String> fields) throws ApiException {
		String url = setFields("mailboxes/" + mailboxId + "/folders.json", fields);
		return getPage(url, Folder.class, 200);
	}

	public Page getConversationsForFolder(Integer mailboxID, Integer folderID) throws ApiException {
		return getPage("mailboxes/" + mailboxID + "/folders/" + folderID + "/conversations.json", Conversation.class, 200);
	}

	public Page getConversationsForFolder(Integer mailboxID, Integer folderID, List<String> fields) throws ApiException {
		String url = setFields("mailboxes/" + mailboxID + "/folders/" + folderID + "/conversations.json", fields);
		return getPage(url, Conversation.class, 200);
	}

	public Page getConversationsForMailbox(Integer mailboxID) throws ApiException {
		return getPage("mailboxes/" + mailboxID + "/conversations.json", Conversation.class, 200);
	}

	public Page getConversationsForMailbox(Integer mailboxID, List<String> fields) throws ApiException {
		String url = setFields("mailboxes/" + mailboxID + "/conversations.json", fields);
		return getPage(url, Conversation.class, 200);
	}

	public Page getConversationsForCustomerByMailbox(Integer mailboxID, Integer customerID) throws ApiException {
		return getPage("mailboxes/" + mailboxID + "/customers/" + customerID + "/conversations.json", Conversation.class, 200);
	}

	public Page getConversationsForCustomerByMailbox(Integer mailboxID, Integer customerID, List<String> fields) throws ApiException {
		String url = setFields("mailboxes/" + mailboxID + "/customers/" + customerID + "/conversations.json", fields);
		return getPage(url, Conversation.class, 200);
	}

	public Conversation getConversation(Long conversationID) throws ApiException {
		return (Conversation)getItem("conversations/" + conversationID + ".json", Conversation.class, 200);
	}

	public Conversation getConversation(Integer conversationID, List<String> fields) throws ApiException {
		if (conversationID == null || conversationID < 1) {
			throw new ApiException("Invalid conversationId in getConversation");
		}
		String url = setFields("conversations/" + conversationID + ".json", fields);
		return (Conversation)getItem(url, Conversation.class, 200);
	}

	public String getAttachmentData(Integer attachmentID) throws ApiException {
		if (attachmentID == null || attachmentID < 1) {
			throw new ApiException("Invalid attachmentID in getAttachmentData");
		}
		String json = doGet("attachments/" + attachmentID + "/data.json", 200);
		JsonElement obj = (new JsonParser()).parse(json);
		JsonElement elem  = obj.getAsJsonObject().get("item");
		return getDecoded(elem.getAsJsonObject().get("data").getAsString());
	}

	public Page getCustomers() throws ApiException {
		return getCustomers(new Integer(null));
	}

	public Page getCustomers(Integer page) throws ApiException {
		if (page != null) {
			return getPage("customers.json?page=" + page, Customer.class, 200);
		} else {
			return getPage("customers.json", Customer.class, 200);
		}
	}

	public Page getCustomers(List<String> fields) throws ApiException {
		String url = setFields("customers.json", fields);
		return getPage(url, Customer.class, 200);
	}

	public Customer getCustomer(Long customerId) throws ApiException {
		return (Customer)getItem("customers/" + customerId + ".json", Customer.class, 200);
	}

	public Customer getCustomer(Long customerId, List<String> fields) throws ApiException {
		if (customerId == null || customerId < 1) {
			throw new ApiException("Invalid customerId in getCustomer");
		}
		String url = setFields("customers/" + customerId + ".json", fields);
		return (Customer)getItem(url, Customer.class, 200);
	}

	public User getUser(Integer userID) throws ApiException {
		return (User)getItem("users/" + userID + ".json", User.class, 200);
	}

	public User getUser(Integer userID, List<String> fields) throws ApiException {
		if (userID == null || userID < 1) {
			throw new ApiException("Invalid userId in getUser");
		}
		String url = setFields("users/" + userID + ".json", fields);
		return (User)getItem(url, User.class, 200);
	}

	public Page getUsers() throws ApiException {
		return getPage("users.json", User.class, 200);
	}

	public Page getUsers(List<String> fields) throws ApiException {
		String url = setFields("users.json", fields);
		return getPage(url, User.class, 200);
	}

	public Page getUsersForMailbox(Integer mailboxId) throws ApiException {
		return getPage("mailboxes/" + mailboxId + "/users.json", User.class, 200);
	}

	public Page getUsersForMailbox(Integer mailboxId, List<String> fields) throws ApiException {
		String url = setFields("mailboxes/" + mailboxId + "/users.json", fields);
		return getPage(url, User.class, 200);
	}

	public void createCustomer(Customer customer) throws ApiException {
		String json = new Gson().toJson(customer);
		Long id = doPost("customers.json", json, 201);
		customer.setId(id);
	}

	public void updateCustomer(Customer customer) throws ApiException {
		GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String json = builder.create().toJson(customer, Customer.class);
		doPut("customers/" + customer.getId() + ".json", json, 200);
	}

	public void createConversation(Conversation conversation) throws ApiException {
		GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
				.registerTypeAdapter(ThreadState.class, new ThreadStateAdapter())
				.registerTypeAdapter(Status.class, new StatusAdapter());
		builder.registerTypeAdapter(LineItem.class, new ThreadsAdapater(builder));

		String json = builder.create().toJson(conversation);
		log.debug("BKD => Conversation JSON: " + json);
		Long id = doPost("conversations.json", json, 201);
		conversation.setId(id);
	}

	public void updateConversation(Conversation conversation) throws ApiException {
		GsonBuilder builder = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
				.registerTypeAdapter(ThreadState.class, new ThreadStateAdapter())
				.registerTypeAdapter(Status.class, new StatusAdapter());

		String json = builder.create().toJson(conversation, Conversation.class);
		doPut("conversations/" + conversation.getId() + ".json", json, 200);
	}

	private String setFields(String url, List<String> fields) {
		if (fields != null && fields.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(url + "?fields=");
		    String sep = "";
		    Iterator<String> iterator = fields.iterator();
		    while (iterator.hasNext()) {
		    	sb.append(sep).append(iterator.next());
		        sep = ",";
		    }
		    url = sb.toString();
		}
		return url;
	}

	private Object getItem(String url, Class<?> clazzType, int expectedCode) throws ApiException {
		String json = doGet(url, expectedCode);
		JsonElement obj  = (new JsonParser()).parse(json);
		JsonElement item = obj.getAsJsonObject().get("item");

		return Parser.getInstance().getObject(item, clazzType);
	}

	private Page getPage(String url, Class<?> clazzType, int expectedCode) throws ApiException {
		String json = doGet(url, 200);
		JsonElement obj = (new JsonParser()).parse(json);

		Set<Map.Entry<String, JsonElement>> set = obj.getAsJsonObject().entrySet();

		Page p = new Page();

		Iterator<Map.Entry<String, JsonElement>> elem = set.iterator();
		while(elem.hasNext()) {
			Map.Entry<String, JsonElement> a = elem.next();
			String key      = a.getKey();
			JsonElement val = a.getValue();

			if (key.equals("page")) {
				p.setPage(val.getAsInt());
				continue;
			}
			if (key.equals("pages")) {
				p.setPages(val.getAsInt());
				continue;
			}
			if (key.equals("count")) {
				p.setCount(val.getAsInt());
				continue;
			}
			if (key.equals("items")) {
				p.setItems(getPageItems(val, clazzType));
			}
		}
		return p;
	}

	private ArrayList<Object> getPageItems(JsonElement elem, Class<?> clazzType) {
		Gson gson = new Gson();
		JsonArray ar = elem.getAsJsonArray();

		ArrayList<Object> col = new ArrayList<Object>(ar.size());
		for(JsonElement e : ar) {
			try {
				Object o = gson.fromJson(e, clazzType);
				if (o != null) {
					col.add(o);
				}
			} catch (Exception ex) {
				log.debug("BKD => " + e.toString());
			}
		}
		return col;
	}

	private Long doPost(String url, String requestBody, int expectedCode) throws ApiException {
		HttpURLConnection conn = null;
		Long id = null;
		try {
		    conn = getConnection(apiKey, url, METHOD_POST);

			if (requestBody != null) {
				conn.setDoOutput(true);
				OutputStream output = null;
				try {
					output = conn.getOutputStream();
					output.write(requestBody.getBytes("UTF-8"));
				} finally {
					if (output != null) {
						try { output.close(); } catch (IOException ioe) {}
					}
				}
			}

			conn.connect();
			checkStatusCode(conn, expectedCode);

			String location = conn.getHeaderField("LOCATION");
			id = new Long(location.substring(location.lastIndexOf("/") + 1, location.lastIndexOf(".")));

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			close(conn);
		}
		return id;
	}

	private void doPut(String url, String requestBody, int expectedCode) throws ApiException {
		HttpURLConnection conn = null;
		Long id = null;
		try {
			conn = getConnection(apiKey, url, METHOD_PUT);

			if (requestBody != null) {
				conn.setDoOutput(true);
				OutputStream output = null;
				try {
					output = conn.getOutputStream();
					output.write(requestBody.getBytes("UTF-8"));
				} finally {
					if (output != null) {
						try { output.close(); } catch (IOException ioe) {}
					}
				}
			}

			conn.connect();
			checkStatusCode(conn, expectedCode);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			close(conn);
		}
	}

	private String doGet(String url, int expectedCode) throws ApiException {
		HttpURLConnection conn = null;

		BufferedReader br  = null;
		String response    = null;

		try {
			conn = getConnection(apiKey, url, METHOD_GET);
			conn.connect();
			checkStatusCode(conn, expectedCode);

			br = new BufferedReader(new InputStreamReader((getInputStream(conn)), Charset.forName("UTF8")));
			response = getResponse(br);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(br);
			close(conn);
		}
		return response;
	}

	private HttpURLConnection getConnection(String apiKey, String url, String method) throws Exception {
		URL aUrl = new URL(BASE_URL + url);

		HttpURLConnection conn = (HttpURLConnection) aUrl.openConnection();

		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod(method);

		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Authorization", "Basic " + getEncoded(apiKey + ":x"));
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
		return conn;
	}

	private void checkStatusCode(HttpURLConnection conn, int expectedCode) throws ApiException, IOException {
		int code = conn.getResponseCode();
		if (code == expectedCode) {
			return;
		}
		switch(code) {
			case 400:
				throw new InvalidFormatException("The request was not formatted correctly");
			case 401:
				throw new InvalidApiKeyException("Invalid API key");
			case 402:
				throw new ApiKeySuspendedException("API key suspended");
			case 403:
				throw new AccessDeniedException("Access denied");
			case 404:
				throw new NotFoundException("Resource not found");
			case 405:
				throw new InvalidMethodException("Invalid method type");
			case 429:
				throw new ThrottleRateException("Throttle limit reached. Too many requests");
			case 500:
				throw new ServerException("Application error or server error");
			case 503:
				throw new ServiceUnavailableException("Service Temporarily Unavailable");
			default:
				throw new ApiException("API key suspended");
		}
	}

	private String getResponse(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();

    	String line;
    	while ((line = reader.readLine()) != null) {
    		sb.append(line);
    	}

    	return sb.toString();
	}

	private void close(HttpURLConnection conn) {
		if (conn != null) {
			try {
				conn.disconnect();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	private void close(BufferedReader reader) {
		if (reader != null) {
    		try {
    			reader.close();
			} catch (IOException e) {
				// ignore
			}
    	}
	}

	private InputStream getInputStream(HttpURLConnection conn) throws IOException {
        String encoding = conn.getContentEncoding();

		InputStream inputStream = null;

		//create the appropriate stream wrapper based on
		//the encoding type
		if (encoding != null) {
			if (encoding.equalsIgnoreCase("gzip")) {
				inputStream = new GZIPInputStream(conn.getInputStream());
			}
			else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
				inputStream = new InflaterInputStream(conn.getInputStream(), new Inflater(true));
			}
		}
		if (inputStream == null) {
			inputStream = conn.getInputStream();
		}
		return inputStream;
	}

	private String getEncoded(String val) {
		return (new BASE64Encoder()).encode(val.getBytes());
	}

	private String getDecoded(String val) {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			return new String(decoder.decodeBuffer(val));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}