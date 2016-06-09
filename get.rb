#! /usr/bin/env ruby

require 'JSON'
require 'OpenSSL'
require 'Base64'

STORE = "data.json"

def usage
  puts "Usage: \tget.rb -l\n\tget.rb <site> <secret>"
  exit 1
end

def load
  data_file = File.open( STORE, "r" )
  data_raw = data_file.read
  data = JSON.parse( data_raw, { symbolize_names: true } )
end

def get_obj data, name
  data.select { |i| i[:site] == name }[0]
end

command = ARGV[0]
if !command then
  usage
end

if command == "-l" && ARGV.length == 1 then
  data = load
  puts data.map {|obj| obj[:site] + " @ " + obj[:version] }
elsif ARGV.length == 2 then
  data = load

  name = ARGV[0]
  secret = ARGV[1]

  obj = get_obj data, name

  if !obj then
    puts "No data found."
    exit 2
  end

  input = [ secret, name, obj[:version] ].join( "_" )

  hasher = OpenSSL::Digest::SHA256.new
  base64 = Base64.encode64( hasher.digest( input ) )

  puts base64.slice( 0, obj[:length] )
else
  usage
end
