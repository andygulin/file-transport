// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from TransportPiece.proto

package file.transport.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.dyuproject.protostuff.ByteString;
import com.dyuproject.protostuff.GraphIOUtil;
import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Message;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.UninitializedMessageException;

public final class TransportPiece implements Externalizable, Message<TransportPiece> {
	public enum WriteMode implements com.dyuproject.protostuff.EnumLite<WriteMode> {
		OVERWRITE(1), SKIP(2), DELETE(3);

		public final int number;

		private WriteMode(int number) {
			this.number = number;
		}

		public int getNumber() {
			return number;
		}

		public static WriteMode valueOf(int number) {
			switch (number) {
			case 1:
				return OVERWRITE;
			case 2:
				return SKIP;
			case 3:
				return DELETE;
			default:
				return null;
			}
		}
	}

	public static Schema<TransportPiece> getSchema() {
		return SCHEMA;
	}

	public static TransportPiece getDefaultInstance() {
		return DEFAULT_INSTANCE;
	}

	static final TransportPiece DEFAULT_INSTANCE = new TransportPiece();

	// non-private fields
	// see
	// http://developer.android.com/guide/practices/design/performance.html#package_inner
	String dest;
	ByteString content;
	WriteMode mode;
	Integer pieceNum;

	public TransportPiece() {

	}

	public TransportPiece(String dest) {
		this.dest = dest;
	}

	// getters and setters

	// dest

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	// content

	public ByteString getContent() {
		return content;
	}

	public void setContent(ByteString content) {
		this.content = content;
	}

	// mode

	public WriteMode getMode() {
		return mode == null ? WriteMode.OVERWRITE : mode;
	}

	public void setMode(WriteMode mode) {
		this.mode = mode;
	}

	// pieceNum

	public Integer getPieceNum() {
		return pieceNum;
	}

	public void setPieceNum(Integer pieceNum) {
		this.pieceNum = pieceNum;
	}

	// java serialization

	public void readExternal(ObjectInput in) throws IOException {
		GraphIOUtil.mergeDelimitedFrom(in, this, SCHEMA);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		GraphIOUtil.writeDelimitedTo(out, this, SCHEMA);
	}

	// message method

	public Schema<TransportPiece> cachedSchema() {
		return SCHEMA;
	}

	static final Schema<TransportPiece> SCHEMA = new Schema<TransportPiece>() {
		// schema methods

		public TransportPiece newMessage() {
			return new TransportPiece();
		}

		public Class<TransportPiece> typeClass() {
			return TransportPiece.class;
		}

		public String messageName() {
			return TransportPiece.class.getSimpleName();
		}

		public String messageFullName() {
			return TransportPiece.class.getName();
		}

		public boolean isInitialized(TransportPiece message) {
			return message.dest != null;
		}

		public void mergeFrom(Input input, TransportPiece message) throws IOException {
			for (int number = input.readFieldNumber(this);; number = input.readFieldNumber(this)) {
				switch (number) {
				case 0:
					return;
				case 1:
					message.dest = input.readString();
					break;
				case 2:
					message.content = input.readBytes();
					break;
				case 3:
					message.mode = WriteMode.valueOf(input.readEnum());
					break;
				case 4:
					message.pieceNum = input.readInt32();
					break;
				default:
					input.handleUnknownField(number, this);
				}
			}
		}

		public void writeTo(Output output, TransportPiece message) throws IOException {
			if (message.dest == null)
				throw new UninitializedMessageException(message);
			output.writeString(1, message.dest, false);

			if (message.content != null)
				output.writeBytes(2, message.content, false);

			if (message.mode != null)
				output.writeEnum(3, message.mode.number, false);

			if (message.pieceNum != null)
				output.writeInt32(4, message.pieceNum, false);
		}

		public String getFieldName(int number) {
			switch (number) {
			case 1:
				return "dest";
			case 2:
				return "content";
			case 3:
				return "mode";
			case 4:
				return "pieceNum";
			default:
				return null;
			}
		}

		public int getFieldNumber(String name) {
			final Integer number = fieldMap.get(name);
			return number == null ? 0 : number.intValue();
		}

		final java.util.HashMap<String, Integer> fieldMap = new java.util.HashMap<String, Integer>();
		{
			fieldMap.put("dest", 1);
			fieldMap.put("content", 2);
			fieldMap.put("mode", 3);
			fieldMap.put("pieceNum", 4);
		}
	};

}
