# gs-avro-tools
Read avro files directly from google cloud storage. Also utilises a `LocalSchemaRegistry` to view serialised avro (currently `Envelope`)

This hijacks `GenericData.java` with a local version in order to allow custom `toString` formatting based on a field name and type.

## Preqrequisites
1. Install the Google Cloud SDK from https://cloud.google.com/sdk/downloads#interactive
2. Execute `gcloud auth application-default login` (you must have access to the releavant buckets)

## Installation
1. Copy the `gs-avro-tools` script in the base directory to your `~/bin` or somewhere else on the path (update `INSTALL_DIR` to whatever you choose)
2. Execute `chmod 700 ~/bin/gs-avro-tools`
3. Run `gs-avro-tools install`
4. Run `gs-avro-tools --help` to verify successful installation

## Usage

```
GS Avro Tools v0.3
  -l, --localrepo  <arg>   Base directory containing commons schemas. Required
                           for Envelope deserialisation
  -h, --help               Show help message
  -v, --version            Show version of this program

Subcommand: count
  -a, --avro  <arg>   Location of avro file locally or in google storage (gs://)
  -h, --help          Show help message
Subcommand: tojson
  -a, --avro  <arg>     Location of avro file locally or in google storage
                        (gs://)
  -h, --human           (BETA) Attempt to make data such as timestamps and ips
                        human readable.
      --nounwrap        [Envelopes only] Will not unwrap body
  -n, --number  <arg>   Default: 5. Number of records to show. < 0 will exhaust
                        the stream
  -p, --pretty          Pretty print the output
  -x, --x               [Envelopes only] If unwrapping, show message type and
                        version
      --help            Show help message
Subcommand: getschema
  -a, --avro  <arg>   Location of avro file locally or in google storage (gs://)
  -h, --help          Show help message
```

## Examples
```gs-avro-tools tojson --avro gs://fq-logs-merged/pixel/2019/06/07/15/part-r-132669_0dccc86b-517b-4ae2-a615-1c93407788ac-00229.avro -n 1 --pretty -x -h```


