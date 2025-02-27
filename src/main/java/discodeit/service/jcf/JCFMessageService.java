package discodeit.service.jcf;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private Map<String, Message> messageData = new HashMap<>();

    @Override
    public Message create(Message newMessage) {
        String messageId = newMessage.getMessageId();
        String messageDetail = newMessage.getMessageDetail();
        User sendUser = newMessage.getSendUser();
        User receiveUser = newMessage.getReceiveUser();

        // 예외처리
        if (isMessageIdDuplicate(messageId)) {
            throw new IllegalArgumentException("[error] 이미 존재하는 메세지 ID입니다.");
        }
        if (messageDetail == null || messageDetail.isEmpty()) {
            throw new IllegalArgumentException("[error] 유효하지 않은 메세지 형식입니다.");
        }
        if (sendUser.getUserId() == null || sendUser.getUserId().isEmpty()) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자는 메세지를 전송할 수 없습니다.");
        }
        if (receiveUser.getUserId() == null || receiveUser.getUserId().isEmpty()) {
            throw new IllegalArgumentException("[error] 존재하지 않는 사용자에게 메세지를 전송할 수 없습니다.");
        }

        messageData.put(messageId, newMessage);
        return newMessage;
    }

    @Override
    public Message readById(String messageId) {
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId);
    }

    @Override
    public List<Message> readByChannel(String channelId) {
        List<String> channelList = messageData.values().stream().map(message -> message.getChannel().getChannelId()).toList();
        if (!channelList.contains(channelId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 채널 ID입니다.");
        }
        return messageData.values().stream().filter(message -> message.getChannel().getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Message> readAll() {
        return messageData.values().stream().toList();
    }

    @Override
    public Message updateMessage(String messageId, Message updateMessage) {
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        Message originMessage = messageData.get(messageId);

        originMessage.updateMessageDetail(updateMessage.getMessageDetail());
        return originMessage;
    }

    @Override
    public void delete(String messageId) {
        if (!messageData.containsKey(messageId)) {
            throw new RuntimeException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        messageData.remove(messageId);
        System.out.println("[삭제 완료]");
    }

    @Override
    public void deleteByChannel(Channel channel) {
        messageData.values().removeIf(message -> message.getChannel().equals(channel));
    }

    @Override
    public Channel getChannel(String messageId) {
        if (!messageData.containsKey(messageId)) {
            throw new IllegalArgumentException("[error] 존재하지 않는 메세지 ID입니다.");
        }
        return messageData.get(messageId).getChannel();
    }


    private boolean isMessageIdDuplicate(String messageId) {
        return messageData.containsKey(messageId);
    }

}
