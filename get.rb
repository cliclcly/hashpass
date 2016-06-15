#! /usr/bin/env ruby

require 'optparse'
require 'JSON'
require 'OpenSSL'
require 'Base64'
require 'Pathname'

STORE = "#{Dir.home}/.hashpass/data.json"

def usage
  puts "Usage: \tget.rb -l\n\tget.rb <site> <secret>"
  exit 1
end

def load opts
  data_file = File.open( opts[:source], "r" )
  data_raw = data_file.read
  data = JSON.parse( data_raw, { symbolize_names: true } )
end

def get_obj data, name
  data.select { |i| i[:site] == name }[0]
end

options = { source: STORE,
            get: true,
          }

parser = OptionParser.new do |opts|
  opts.banner = "Usage: get.rb [options] SITE_ID"

  opts.separator "Commands:"

  opts.on("-g", "--get", "(default) Get password") do |g|
    options[:get] = g
  end

  opts.on("-l", "--list", "List site id's and versions") do |v|
    options[:list] = v
    options[:get] = false;
  end

  opts.on("-e", "--encrypt", "Encrypt source file") do |e|
    options[:encrypt] = e
  end

  opts.separator ""
  opts.separator "Universal options:"

  opts.on("-s", "--source PATH", "Get data file from PATH") do |s|
    options[:source] = s
  end

  opts.on("-h", "--help", "Show this message") do
    puts opts
    exit
  end

  opts.separator ""
  opts.separator "Get options:"

  opts.on("-k", "--key KEY", "Secret key used for password generation") do |k|
    options[:key] = k
  end
end

parser.parse!

if options[:get] && options[:list] then
  puts parser.help
  exit
end

if options[:list] then
  data = load options
  puts data.map {|obj| obj[:site] + " @ " + obj[:version] }
end

if options[:get] then
  unless options[:key] then parser.help; exit end

  data = load options

  name = ARGV[0]
  secret = options[:key]

  obj = get_obj data, name

  if !obj then
    puts "No data found"
    exit 2
  end

  input = [ secret, name, obj[:version] ].join( "_" )

  hasher = OpenSSL::Digest::SHA256.new
  base64 = Base64.encode64( hasher.digest( input ) )

  puts base64.slice( 0, obj[:length] )
end
