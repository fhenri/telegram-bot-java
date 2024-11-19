package io.cloud06.gmeet;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GMeetBot extends TelegramLongPollingBot {

    private final String botName;

    public GMeetBot(String botToken, String botName) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()){
            if(msg.getText().equals("/list"))
                listAllRooms(id);
        }

        sendText(id, "hello back");
    }

    private void listAllRooms(Long id) {
        String urlString = "https://booking-room.cloud06.io/api/room";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray rooms = jsonResponse.getJSONArray("rooms");
                StringBuilder result = new StringBuilder("This is the list of all rooms:\n");

                for (int i = 0; i < rooms.length(); i++) {
                    JSONObject room = rooms.getJSONObject(i);
                    String name = room.getString("name");
                    String capacity = room.getString("capacity");
                    result.append("Name: ").append(name).append(", Capacity: ").append(capacity).append("\n");
                }

                sendText(id, result.toString());
            } else {
                sendText(id, "Failed to fetch the list of rooms. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            sendText(id, "An error occurred while fetching the list of rooms: " + e.getMessage());
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
