#ZEEBE_ADDRESS=localhost:26500
#ZEEBE_SECURE_CONNECTION=false
#ZEEBE_BASIC_AUTH_PASSWORD=demo
#ZEEBE_BASIC_AUTH_USERNAME=demo

# "Cancel reservation 메세지 보내기"
#zbctl --insecure publish message "Message_cancel-reservation" --correlationKey "isbn1234_msjo" \
#--variables "{\"from\" : \"zbctl\" }"

# "Order cancelled 메세지 보내기"
#zbctl --insecure publish message "Message_order-cancelled" --correlationKey "isbn1234_msjo" \
#--variables "{\"from\" : \"zbctl\" }"

# "Book returned 메세지 보내기"
#zbctl --insecure publish message "Message_book-returned" --correlationKey "isbn1234_msjo" \
#--variables "{\"from\" : \"zbctl\" }"
